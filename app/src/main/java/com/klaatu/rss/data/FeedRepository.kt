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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class FeedRepository(context: Context) {

    private val dao = KlaatuDatabase.getInstance(context).dao()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    fun observeFeeds(): Flow<List<FeedEntity>> = dao.observeFeeds()
    fun observeCategories(): Flow<List<String>> = dao.observeCategories()
    fun observeTotalArticleCount(): Flow<Int> = dao.observeTotalArticleCount()
    fun observeArticles(category: String?, limit: Int = 150): Flow<List<ArticleWithFeed>> =
        dao.observeArticles(category, limit)

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
        // Update category/title for any existing feeds so re-importing OPML updates folders cleanly
        parsed.forEachIndexed { index, opmlFeed ->
            if (inserted[index] == -1L) {
                dao.updateFeedCategoryByUrl(
                    xmlUrl = opmlFeed.xmlUrl,
                    category = opmlFeed.category,
                    title = opmlFeed.title,
                )
            }
        }
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
        if (feeds.isEmpty()) return@withContext 0
        val syncedCount = AtomicInteger(0)
        val semaphore = Semaphore(8)

        coroutineScope {
            feeds.map { feed ->
                async {
                    semaphore.withPermit {
                        runCatching {
                            val initialUrl = if (feed.xmlUrl.startsWith("http://", ignoreCase = true)) {
                                "https://" + feed.xmlUrl.substring(7)
                            } else {
                                feed.xmlUrl
                            }
                            val response = executeFeedRequest(initialUrl)
                                ?: if (initialUrl != feed.xmlUrl) executeFeedRequest(feed.xmlUrl) else null
                            response?.use { res ->
                                val bodyStream = res.body?.byteStream() ?: return@use
                                val finalUrl = res.request.url.toString()
                                val upgradedUrl = when {
                                    finalUrl.startsWith("https://", ignoreCase = true) -> finalUrl
                                    initialUrl != feed.xmlUrl && res.request.url.isHttps -> initialUrl
                                    else -> null
                                }
                                if (upgradedUrl != null && upgradedUrl != feed.xmlUrl) {
                                    runCatching { dao.updateFeedUrl(feed.id, upgradedUrl) }
                                }
                                val parsed = FeedParser.parse(bodyStream, upgradedUrl ?: feed.xmlUrl)
                                val resolvedTitle = parsed.title.ifBlank { feed.title }
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
                                syncedCount.incrementAndGet()
                            }
                        }
                    }
                }
            }.awaitAll()
        }
        syncedCount.get()
    }

    private fun executeFeedRequest(url: String): okhttp3.Response? = runCatching {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "KlaatuRSS/0.1 (Android)")
            .build()
        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            response
        } else {
            response.close()
            null
        }
    }.getOrNull()

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
