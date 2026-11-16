package com.klaatu.rss.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

data class WidgetArticlePreview(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val url: String,
)

class KlaatuWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val sampleArticles = listOf(
            WidgetArticlePreview(
                id = "1",
                title = "Welcome to Klaatu — Add your OPML feeds to get started",
                source = "Klaatu",
                timeAgo = "Just now",
                url = "https://github.com/sengelbert/klaatu",
            ),
        )

        provideContent {
            GlanceTheme {
                KlaatuWidgetContent(articles = sampleArticles)
            }
        }
    }
}

@Composable
private fun KlaatuWidgetContent(articles: List<WidgetArticlePreview>) {
    val headerBlue = ColorProvider(Color(0xFF39A7F0))
    val cardBg = ColorProvider(Color(0xFFFFFFFF))
    val primaryText = ColorProvider(Color(0xFF1A1C1E))
    val secondaryText = ColorProvider(Color(0xFF5F6368))
    val dividerColor = ColorProvider(Color(0xFFE6E8EB))
    val thumbPlaceholder = ColorProvider(Color(0xFFE1E5EA))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cardBg)
            .cornerRadius(16.dp),
    ) {
        // Header bar
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(headerBlue)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Klaatu",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = "All Feeds (${articles.size})",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFE3F2FD)),
                        fontSize = 12.sp,
                    ),
                )
            }
        }

        // Scrollable article list
        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
            items(articles, itemId = { it.id.hashCode().toLong() }) { article ->
                val openInBrowser = actionStartActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                )
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(openInBrowser),
                ) {
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = GlanceModifier
                                .size(width = 88.dp, height = 60.dp)
                                .background(thumbPlaceholder)
                                .cornerRadius(8.dp),
                        ) {}
                        Spacer(modifier = GlanceModifier.width(12.dp))
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = article.title,
                                maxLines = 2,
                                style = TextStyle(
                                    color = primaryText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = "${article.source} · ${article.timeAgo}",
                                maxLines = 1,
                                style = TextStyle(
                                    color = secondaryText,
                                    fontSize = 12.sp,
                                ),
                            )
                        }
                    }
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor),
                    ) {}
                }
            }
        }
    }
}
