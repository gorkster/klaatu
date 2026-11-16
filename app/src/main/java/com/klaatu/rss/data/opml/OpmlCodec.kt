package com.klaatu.rss.data.opml

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

data class OpmlFeed(
    val title: String,
    val xmlUrl: String,
    val htmlUrl: String = "",
    val category: String = DEFAULT_CATEGORY,
) {
    companion object {
        const val DEFAULT_CATEGORY = "Uncategorized"
    }
}

object OpmlCodec {

    fun parse(inputStream: InputStream): List<OpmlFeed> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
        }
        val document = factory.newDocumentBuilder().parse(inputStream)
        document.documentElement.normalize()

        val body = findFirstElementByTagIgnoreCase(document.documentElement, "body") ?: return emptyList()
        val results = mutableListOf<OpmlFeed>()
        collectOutlines(body, currentCategory = null, out = results)
        return results.distinctBy { it.xmlUrl }
    }

    private fun collectOutlines(
        parent: Node,
        currentCategory: String?,
        out: MutableList<OpmlFeed>,
    ) {
        val children = parent.childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType != Node.ELEMENT_NODE || !node.nodeName.equals("outline", ignoreCase = true)) {
                continue
            }
            val element = node as Element
            val xmlUrl = attrIgnoreCase(element, "xmlUrl").trim()
            val label = attrIgnoreCase(element, "title").ifBlank { attrIgnoreCase(element, "text") }.trim()

            if (xmlUrl.isNotEmpty()) {
                val explicitCategory = attrIgnoreCase(element, "category")
                    .trim()
                    .trimStart('/')
                    .substringBefore(',')
                    .trim()
                val resolvedCategory = explicitCategory
                    .ifBlank { currentCategory.orEmpty() }
                    .ifBlank { OpmlFeed.DEFAULT_CATEGORY }

                out += OpmlFeed(
                    title = label.ifBlank { xmlUrl },
                    xmlUrl = xmlUrl,
                    htmlUrl = attrIgnoreCase(element, "htmlUrl").trim(),
                    category = resolvedCategory,
                )
            } else {
                val nextCategory = label.ifBlank { currentCategory }
                collectOutlines(element, nextCategory, out)
            }
        }
    }

    private fun findFirstElementByTagIgnoreCase(root: Element, tagName: String): Element? {
        if (root.tagName.equals(tagName, ignoreCase = true)) return root
        val children = root.childNodes
        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val found = findFirstElementByTagIgnoreCase(node as Element, tagName)
                if (found != null) return found
            }
        }
        return null
    }

    private fun attrIgnoreCase(element: Element, name: String): String {
        val direct = element.getAttribute(name)
        if (direct.isNotEmpty()) return direct
        val attrs = element.attributes
        for (i in 0 until attrs.length) {
            val attr = attrs.item(i)
            if (attr.nodeName.equals(name, ignoreCase = true)) {
                return attr.nodeValue.orEmpty()
            }
        }
        return ""
    }

    fun export(feeds: List<OpmlFeed>, documentTitle: String = "Klaatu Subscriptions"): String {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val opml = doc.createElement("opml").apply { setAttribute("version", "2.0") }
        doc.appendChild(opml)

        val head = doc.createElement("head")
        val titleEl = doc.createElement("title").apply { textContent = documentTitle }
        head.appendChild(titleEl)
        opml.appendChild(head)

        val body = doc.createElement("body")
        opml.appendChild(body)

        val grouped = feeds.groupBy { it.category.ifBlank { OpmlFeed.DEFAULT_CATEGORY } }
        for ((category, categoryFeeds) in grouped) {
            val container: Element = if (category == OpmlFeed.DEFAULT_CATEGORY) {
                body
            } else {
                doc.createElement("outline").apply {
                    setAttribute("text", category)
                    setAttribute("title", category)
                    body.appendChild(this)
                }
            }
            for (feed in categoryFeeds) {
                val feedEl = doc.createElement("outline").apply {
                    setAttribute("type", "rss")
                    setAttribute("text", feed.title)
                    setAttribute("title", feed.title)
                    setAttribute("xmlUrl", feed.xmlUrl)
                    if (feed.htmlUrl.isNotBlank()) {
                        setAttribute("htmlUrl", feed.htmlUrl)
                    }
                }
                container.appendChild(feedEl)
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        }
        val writer = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(writer))
        return writer.toString()
    }
}
