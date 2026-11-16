package com.klaatu.rss

import com.klaatu.rss.data.opml.OpmlCodec
import com.klaatu.rss.data.opml.OpmlFeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpmlCodecTest {

    @Test
    fun parse_nestedCategoriesAndFlatFeeds() {
        val opml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <opml version="2.0">
              <head><title>Subscriptions</title></head>
              <body>
                <outline text="Android + Linux" title="Android + Linux">
                  <outline type="rss" text="9to5Linux" title="9to5Linux" xmlUrl="https://9to5linux.com/feed" htmlUrl="https://9to5linux.com" />
                  <outline type="rss" text="9to5Google" xmlUrl="https://9to5google.com/feed" />
                </outline>
                <outline type="rss" text="Kotlin Blog" xmlUrl="https://blog.jetbrains.com/kotlin/feed/" />
              </body>
            </opml>
        """.trimIndent()

        val feeds = OpmlCodec.parse(opml.byteInputStream())
        assertEquals(3, feeds.size)

        assertEquals("9to5Linux", feeds[0].title)
        assertEquals("Android + Linux", feeds[0].category)
        assertEquals("https://9to5linux.com/feed", feeds[0].xmlUrl)

        assertEquals("9to5Google", feeds[1].title)
        assertEquals("Android + Linux", feeds[1].category)

        assertEquals("Kotlin Blog", feeds[2].title)
        assertEquals(OpmlFeed.DEFAULT_CATEGORY, feeds[2].category)
    }

    @Test
    fun export_andReimportRoundTrip() {
        val original = listOf(
            OpmlFeed("9to5Linux", "https://9to5linux.com/feed", "https://9to5linux.com", "Linux"),
            OpmlFeed("LWN", "https://lwn.net/headlines/rss", "", "Linux"),
            OpmlFeed("XKCD", "https://xkcd.com/rss.xml", "", OpmlFeed.DEFAULT_CATEGORY),
        )

        val exported = OpmlCodec.export(original)
        assertTrue(exported.contains("version=\"2.0\""))

        val reimported = OpmlCodec.parse(exported.byteInputStream())
        assertEquals(original, reimported)
    }
}
