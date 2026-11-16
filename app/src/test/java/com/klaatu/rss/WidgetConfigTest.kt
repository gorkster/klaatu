package com.klaatu.rss

import com.klaatu.rss.widget.WidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetConfigTest {

    @Test
    fun resolveSubtitle_allFeedsByDefault() {
        val config = WidgetConfig(appWidgetId = 1)
        assertEquals("All Feeds (42)", config.resolveSubtitle(42))
    }

    @Test
    fun resolveSubtitle_joinsMultipleCategories() {
        val config = WidgetConfig(
            appWidgetId = 2,
            categories = setOf("Linux", "Android"),
        )
        assertEquals("Android + Linux (356)", config.resolveSubtitle(356))
    }

    @Test
    fun resolveSubtitle_prefersCustomLabel() {
        val config = WidgetConfig(
            appWidgetId = 3,
            categories = setOf("Linux", "Android"),
            customLabel = "Tech News",
        )
        assertEquals("Tech News (12)", config.resolveSubtitle(12))
    }
}
