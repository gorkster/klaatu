package com.klaatu.rss.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
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
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.klaatu.rss.R
import com.klaatu.rss.data.FeedRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class WidgetArticleUi(
    val id: Long,
    val title: String,
    val source: String,
    val timeAgo: String,
    val url: String,
    val thumbnail: Bitmap?,
)

class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        FeedRepository(context).syncAllFeeds()
        KlaatuWidget().update(context, glanceId)
    }
}

class KlaatuWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val config = WidgetPreferences.getConfig(context, appWidgetId)
        val repo = FeedRepository(context)

        val totalCount = repo.getWidgetArticleCount(config.categories, config.feedIds)
        val rawArticles = repo.getWidgetArticles(config.categories, config.feedIds, limit = 30)
        val thumbnails = loadThumbnails(context, rawArticles.map { it.id to it.imageUrl })

        val uiArticles = if (rawArticles.isEmpty()) {
            listOf(
                WidgetArticleUi(
                    id = -1L,
                    title = "No articles yet — tap the gear icon to configure feeds or import OPML",
                    source = "Klaatu",
                    timeAgo = "Ready",
                    url = "https://github.com/sengelbert/klaatu",
                    thumbnail = null,
                )
            )
        } else {
            rawArticles.map { article ->
                WidgetArticleUi(
                    id = article.id,
                    title = article.title,
                    source = article.feedTitle,
                    timeAgo = formatRelativeTime(article.publishedAt),
                    url = article.link,
                    thumbnail = thumbnails[article.id],
                )
            }
        }

        val subtitle = config.resolveSubtitle(totalCount)
        val configIntent = Intent(context, WidgetConfigActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse("klaatu://widget/$appWidgetId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        provideContent {
            GlanceTheme {
                KlaatuWidgetContent(
                    theme = config.theme,
                    subtitle = subtitle,
                    configIntent = configIntent,
                    articles = uiArticles,
                )
            }
        }
    }

    private suspend fun loadThumbnails(
        context: Context,
        items: List<Pair<Long, String?>>,
    ): Map<Long, Bitmap> = coroutineScope {
        val imageLoader = context.imageLoader
        items.take(25)
            .filter { !it.second.isNullOrBlank() }
            .map { (id, url) ->
                async {
                    val bitmap = runCatching {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .size(264, 180)
                            .allowHardware(false)
                            .build()
                        val result = imageLoader.execute(request) as? SuccessResult
                        (result?.drawable as? BitmapDrawable)?.bitmap
                    }.getOrNull()
                    if (bitmap != null) id to bitmap else null
                }
            }
            .awaitAll()
            .filterNotNull()
            .toMap()
    }

    private fun formatRelativeTime(publishedAt: Long): String {
        if (publishedAt <= 0L) return ""
        return DateUtils.getRelativeTimeSpanString(
            publishedAt,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE,
        ).toString()
    }
}

@Composable
private fun KlaatuWidgetContent(
    theme: WidgetThemePreset,
    subtitle: String,
    configIntent: Intent,
    articles: List<WidgetArticleUi>,
) {
    val headerBg = ColorProvider(theme.headerBg)
    val headerTitleColor = ColorProvider(theme.headerTitle)
    val headerSubtitleColor = ColorProvider(theme.headerSubtitle)
    val cardBg = ColorProvider(theme.cardBg)
    val primaryText = ColorProvider(theme.primaryText)
    val secondaryText = ColorProvider(theme.secondaryText)
    val dividerColor = ColorProvider(theme.divider)
    val placeholderBg = ColorProvider(theme.placeholderBg)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cardBg)
            .cornerRadius(16.dp),
    ) {
        // Palabre-inspired Header Bar
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(headerBg)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_gort),
                contentDescription = "Klaatu",
                colorFilter = ColorFilter.tint(headerTitleColor),
                modifier = GlanceModifier.size(22.dp),
            )
            Spacer(modifier = GlanceModifier.width(10.dp))
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Klaatu",
                    maxLines = 1,
                    style = TextStyle(
                        color = headerTitleColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = subtitle,
                    maxLines = 1,
                    style = TextStyle(
                        color = headerSubtitleColor,
                        fontSize = 12.sp,
                    ),
                )
            }
            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = "Refresh feeds",
                colorFilter = ColorFilter.tint(headerTitleColor),
                modifier = GlanceModifier
                    .size(28.dp)
                    .padding(4.dp)
                    .clickable(actionRunCallback<RefreshWidgetAction>()),
            )
            Spacer(modifier = GlanceModifier.width(4.dp))
            Image(
                provider = ImageProvider(R.drawable.ic_settings),
                contentDescription = "Configure this widget",
                colorFilter = ColorFilter.tint(headerTitleColor),
                modifier = GlanceModifier
                    .size(28.dp)
                    .padding(4.dp)
                    .clickable(actionStartActivity(configIntent)),
            )
        }

        // Scrollable Article List
        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
            items(articles, itemId = { it.id }) { article ->
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
                        if (article.thumbnail != null) {
                            Image(
                                provider = ImageProvider(article.thumbnail),
                                contentDescription = article.title,
                                contentScale = ContentScale.Crop,
                                modifier = GlanceModifier
                                    .size(width = 88.dp, height = 60.dp)
                                    .cornerRadius(8.dp),
                            )
                        } else {
                            Box(
                                modifier = GlanceModifier
                                    .size(width = 88.dp, height = 60.dp)
                                    .background(placeholderBg)
                                    .cornerRadius(8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    provider = ImageProvider(R.drawable.ic_rss),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(secondaryText),
                                    modifier = GlanceModifier.size(22.dp),
                                )
                            }
                        }
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
                                text = article.source,
                                maxLines = 1,
                                style = TextStyle(
                                    color = secondaryText,
                                    fontSize = 12.sp,
                                ),
                            )
                            if (article.timeAgo.isNotEmpty()) {
                                Text(
                                    text = article.timeAgo,
                                    maxLines = 1,
                                    style = TextStyle(
                                        color = secondaryText,
                                        fontSize = 11.sp,
                                    ),
                                )
                            }
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
