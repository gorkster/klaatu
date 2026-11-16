package com.klaatu.rss.data

import android.content.Context
import com.klaatu.rss.data.db.ArticleEntity
import com.klaatu.rss.data.db.ArticleWithFeed
import com.klaatu.rss.data.db.FeedEntity
import com.klaatu.rss.data.db.KlaatuDatabase
import com.klaatu.rss.data.feed.FeedParser
import com.klaatu.rss.data.opml.OpmlCodec
import com.klaatu.rss.data.opml.OpmlFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit

class FeedRepository(context: Context) {

    private val dao = KlaatuDatabase.getInstance(context).dao()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun observeFeeds(): Flow<List<FeedEntity>> = dao.observeFeeds()
    fun observeCategories(): Flow<List<String>> = dao.observeCategories()
    fun observeTotalArticleCount(): Flow<Int> = dao.observeTotalArticleCount()

    suspend fun importOpml(inputStream: InputStream): Int = withContext(Dispatchers.IO) {
        val parsed = OpmlCodec.parse(inputStream)
        if (parsed.isEmpty()) return@withContext 0
        val entities = parsed.map {
            FeedEntity(
                title = it.title,
                xmlUrl = it.xmlUrl,
                htmlUrl = it.htmlUrl,
                category = it.category,
            )
        }
        val inserted = dao.insertFeeds(entities)
        inserted.count { it != -1L }
    }

    suspend fun exportOpml(): String = withContext(Dispatchers.IO) {
        val feeds = dao.getAllFeeds().map {
            OpmlFeed(
                title = it.title,
                xmlUrl = it.xmlUrl,
                htmlUrl = it.htmlUrl,
                category = it.category,
            )
        }
        OpmlCodec.export(feeds)
    }

    suspend fun addFeed(url: String, category: String = OpmlFeed.DEFAULT_CATEGORY): Boolean =
        withContext(Dispatchers.IO) {
            val cleanUrl = url.trim()
            if (cleanUrl.isEmpty()) return@withContext false
            val ids = dao.insertFeeds(
                listOf(
                    FeedEntity(
                        title = cleanUrl,
                        xmlUrl = cleanUrl,
                        category = category.ifBlank { OpmlFeed.DEFAULT_CATEGORY },
                    )
                )
            )
            ids.firstOrNull() != -1L
        }

    suspend fun deleteFeed(feedId: Long) = withContext(Dispatchers.IO) {
        dao.deleteFeed(feedId)
    }

    suspend fun syncAllFeeds(): Int = withContext(Dispatchers.IO) {
        val feeds = dao.getAllFeeds()
        var syncedCount = 0
        for (feed in feeds) {
            runCatching {
                val request = Request.Builder()
                    .url(feed.xmlUrl)
                    .header("User-Agent", "KlaatuRSS/0.1 (Android)")
                    .build()
                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val bodyStream = response.body?.byteStream() ?: return@use
                    val parsed = FeedParser.parse(bodyStream, feed.xmlUrl)
                    val resolvedTitle = if (feed.title == feed.xmlUrl && parsed.title.isNotBlank()) {
                        parsed.title
                    } else {
                        feed.title
                    }
                    dao.updateFeedMetadata(
                        feedId = feed.id,
                        title = resolvedTitle,
                        htmlUrl = parsed.siteUrl.ifBlank { feed.htmlUrl },
                        fetchedAt = System.currentTimeMillis(),
                    )
                    val articles = parsed.articles.map {
                        ArticleEntity(
                            feedId = feed.id,
                            externalId = "${feed.id}:${it.externalId}",
                            title = it.title,
                            link = it.link,
                            imageUrl = it.imageUrl,
                            publishedAt = it.publishedAt,
                        )
                    }
                    dao.insertArticles(articles)
                    syncedCount++
                }
            }
        }
        syncedCount
    }

    suspend fun getWidgetArticles(
        categories: Set<String>,
        feedIds: Set<Long>,
        limit: Int = 40,
    ): List<ArticleWithFeed> = withContext(Dispatchers.IO) {
        val showAll = if (categories.isEmpty() && feedIds.isEmpty()) 1 else 0
        dao.getFilteredArticles(
            showAll = showAll,
            categories = categories.ifEmpty { setOf("") }.toList(),
            feedIds = feedIds.ifEmpty { setOf(-1L) }.toList(),
            limit = limit,
        )
    }

    suspend fun getWidgetArticleCount(
        categories: Set<String>,
        feedIds: Set<Long>,
    ): Int = withContext(Dispatchers.IO) {
        val showAll = if (categories.isEmpty() && feedIds.isEmpty()) 1 else 0
        dao.getFilteredArticleCount(
            showAll = showAll,
            categories = categories.ifEmpty { setOf("") }.toList(),
            feedIds = feedIds.ifEmpty { setOf(-1L) }.toList(),
        )
    }
}
