package com.klaatu.rss

import com.klaatu.rss.data.feed.FeedParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedParserTest {

    @Test
    fun parseRss2_extractsArticlesAndMediaThumbnail() {
        val rss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/">
              <channel>
                <title>9to5Linux</title>
                <link>https://9to5linux.com</link>
                <item>
                  <title>Linux Kernel 7.3 Released</title>
                  <link>https://9to5linux.com/linux-kernel-7-3</link>
                  <guid>https://9to5linux.com/?p=123</guid>
                  <pubDate>Sun, 15 Nov 2026 12:00:00 GMT</pubDate>
                  <media:thumbnail url="https://9to5linux.com/images/kernel73.jpg" />
                </item>
                <item>
                  <title>Inline Image Fallback</title>
                  <link>https://9to5linux.com/inline</link>
                  <description><![CDATA[<p>Hello</p><img src="https://9to5linux.com/images/inline.png" />]]></description>
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val parsed = FeedParser.parse(rss.byteInputStream(), "https://9to5linux.com/feed")
        assertEquals("9to5Linux", parsed.title)
        assertEquals("https://9to5linux.com", parsed.siteUrl)
        assertEquals(2, parsed.articles.size)

        assertEquals("Linux Kernel 7.3 Released", parsed.articles[0].title)
        assertEquals("https://9to5linux.com/images/kernel73.jpg", parsed.articles[0].imageUrl)
        assertTrue(parsed.articles[0].publishedAt > 0L)

        assertEquals("https://9to5linux.com/images/inline.png", parsed.articles[1].imageUrl)
    }

    @Test
    fun parseAtom_extractsEntriesAndAlternateLink() {
        val atom = """
            <?xml version="1.0" encoding="utf-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom" xmlns:media="http://search.yahoo.com/mrss/">
              <title>Kotlin Blog</title>
              <link rel="alternate" href="https://blog.jetbrains.com/kotlin/" />
              <entry>
                <id>tag:jetbrains.com,2026:kotlin-2</id>
                <title>Kotlin Updates</title>
                <link rel="alternate" href="https://blog.jetbrains.com/kotlin/updates/" />
                <published>2026-11-15T10:30:00Z</published>
                <media:content url="https://blog.jetbrains.com/img/kotlin.png" medium="image" />
              </entry>
            </feed>
        """.trimIndent()

        val parsed = FeedParser.parse(atom.byteInputStream(), "https://blog.jetbrains.com/kotlin/feed/")
        assertEquals("Kotlin Blog", parsed.title)
        assertEquals(1, parsed.articles.size)
        assertEquals("https://blog.jetbrains.com/kotlin/updates/", parsed.articles[0].link)
        assertEquals("https://blog.jetbrains.com/img/kotlin.png", parsed.articles[0].imageUrl)
    }
}
