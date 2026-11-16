package com.klaatu.rss.data.feed

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

data class ParsedArticle(
    val externalId: String,
    val title: String,
    val link: String,
    val imageUrl: String?,
    val publishedAt: Long,
)

data class ParsedFeed(
    val title: String,
    val siteUrl: String,
    val articles: List<ParsedArticle>,
)

object FeedParser {

    private val imgTagRegex = Regex("""<img[^>]+src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)

    fun parse(inputStream: InputStream, fallbackUrl: String): ParsedFeed {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
        }
        val doc = factory.newDocumentBuilder().parse(inputStream)
        val root = doc.documentElement
        root.normalize()

        return when {
            root.localName.equals("feed", ignoreCase = true) ||
                root.tagName.equals("feed", ignoreCase = true) -> parseAtom(root, fallbackUrl)
            else -> parseRss(root, fallbackUrl)
        }
    }

    private fun parseRss(root: Element, fallbackUrl: String): ParsedFeed {
        val channel = firstChildElement(root, "channel") ?: root
        val feedTitle = childText(channel, "title").ifBlank { fallbackUrl }
        val siteUrl = childText(channel, "link").ifBlank { fallbackUrl }
        val articles = childElements(channel, "item").mapNotNull { item ->
            val link = childText(item, "link").trim()
            val title = childText(item, "title").trim()
            if (link.isBlank() || title.isBlank()) return@mapNotNull null

            val guid = childText(item, "guid").trim().ifBlank { link }
            val pubDate = parseTimestamp(childText(item, "pubDate"))
            val imageUrl = extractRssImage(item)

            ParsedArticle(
                externalId = guid,
                title = title,
                link = link,
                imageUrl = imageUrl,
                publishedAt = pubDate,
            )
        }
        return ParsedFeed(title = feedTitle, siteUrl = siteUrl, articles = articles)
    }

    private fun parseAtom(root: Element, fallbackUrl: String): ParsedFeed {
        val feedTitle = childText(root, "title").ifBlank { fallbackUrl }
        val siteUrl = extractAtomLink(root) ?: fallbackUrl
        val articles = childElements(root, "entry").mapNotNull { entry ->
            val link = extractAtomLink(entry).orEmpty()
            val title = childText(entry, "title").trim()
            if (link.isBlank() || title.isBlank()) return@mapNotNull null

            val id = childText(entry, "id").trim().ifBlank { link }
            val timestampText = childText(entry, "published")
                .ifBlank { childText(entry, "updated") }
            val imageUrl = extractMediaImage(entry)
                ?: extractImgFromHtml(childText(entry, "content"))
                ?: extractImgFromHtml(childText(entry, "summary"))

            ParsedArticle(
                externalId = id,
                title = title,
                link = link,
                imageUrl = imageUrl,
                publishedAt = parseTimestamp(timestampText),
            )
        }
        return ParsedFeed(title = feedTitle, siteUrl = siteUrl, articles = articles)
    }

    private fun extractRssImage(item: Element): String? {
        extractMediaImage(item)?.let { return it }

        val enclosures = childElements(item, "enclosure")
        for (enc in enclosures) {
            val type = enc.getAttribute("type").lowercase(Locale.ROOT)
            val url = enc.getAttribute("url").trim()
            if (url.isNotEmpty() && (type.startsWith("image/") || url.looksLikeImage())) {
                return url
            }
        }

        return extractImgFromHtml(childText(item, "encoded"))
            ?: extractImgFromHtml(childText(item, "description"))
    }

    private fun extractMediaImage(element: Element): String? {
        val nodes = element.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) continue
            val el = node as Element
            val name = (el.localName ?: el.tagName).lowercase(Locale.ROOT)
            if (name == "thumbnail" || name == "content") {
                val medium = el.getAttribute("medium").lowercase(Locale.ROOT)
                val type = el.getAttribute("type").lowercase(Locale.ROOT)
                val url = el.getAttribute("url").trim()
                if (url.isNotEmpty() && (name == "thumbnail" || medium == "image" || type.startsWith("image/") || url.looksLikeImage())) {
                    return url
                }
            }
        }
        return null
    }

    private fun extractAtomLink(element: Element): String? {
        val links = childElements(element, "link")
        val alternate = links.firstOrNull {
            val rel = it.getAttribute("rel")
            rel.isBlank() || rel.equals("alternate", ignoreCase = true)
        }
        return alternate?.getAttribute("href")?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun extractImgFromHtml(html: String): String? {
        if (html.isBlank()) return null
        return imgTagRegex.find(html)?.groupValues?.getOrNull(1)?.takeIf { it.startsWith("http") }
    }

    private fun String.looksLikeImage(): Boolean {
        val lower = lowercase(Locale.ROOT).substringBefore('?')
        return lower.endsWith(".jpg") ||
            lower.endsWith(".jpeg") ||
            lower.endsWith(".png") ||
            lower.endsWith(".webp") ||
            lower.endsWith(".gif")
    }

    internal fun parseTimestamp(raw: String): Long {
        val text = raw.trim()
        if (text.isEmpty()) return System.currentTimeMillis()
        return runCatching { Instant.parse(text).toEpochMilli() }
            .recoverCatching { ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME).toInstant().toEpochMilli() }
            .recoverCatching { ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli() }
            .getOrElse { System.currentTimeMillis() }
    }

    private fun firstChildElement(parent: Element, localName: String): Element? =
        childElements(parent, localName).firstOrNull()

    private fun childElements(parent: Element, targetName: String): List<Element> {
        val out = mutableListOf<Element>()
        val children = parent.childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType != Node.ELEMENT_NODE) continue
            val el = node as Element
            val name = el.localName ?: el.tagName
            if (name.equals(targetName, ignoreCase = true)) {
                out += el
            }
        }
        return out
    }

    private fun childText(parent: Element, targetName: String): String =
        firstChildElement(parent, targetName)?.textContent?.trim().orEmpty()
}
