package com.klaatu.rss.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KlaatuDao {

    @Query("SELECT * FROM feeds ORDER BY category ASC, title ASC")
    fun observeFeeds(): Flow<List<FeedEntity>>

    @Query("SELECT * FROM feeds ORDER BY category ASC, title ASC")
    suspend fun getAllFeeds(): List<FeedEntity>

    @Query("SELECT DISTINCT category FROM feeds ORDER BY category ASC")
    fun observeCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeeds(feeds: List<FeedEntity>): List<Long>

    @Query("UPDATE feeds SET title = :title, htmlUrl = :htmlUrl, lastFetchedAt = :fetchedAt WHERE id = :feedId")
    suspend fun updateFeedMetadata(feedId: Long, title: String, htmlUrl: String, fetchedAt: Long)

    @Query("DELETE FROM feeds WHERE id = :feedId")
    suspend fun deleteFeed(feedId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query(
        """
        SELECT a.id, a.title, a.link, a.imageUrl, a.publishedAt, f.title AS feedTitle, f.category AS category
        FROM articles a
        INNER JOIN feeds f ON a.feedId = f.id
        WHERE (:showAll = 1 OR f.category IN (:categories) OR a.feedId IN (:feedIds))
        ORDER BY a.publishedAt DESC
        LIMIT :limit
        """
    )
    suspend fun getFilteredArticles(
        showAll: Int,
        categories: List<String>,
        feedIds: List<Long>,
        limit: Int = 40,
    ): List<ArticleWithFeed>

    @Query(
        """
        SELECT COUNT(*)
        FROM articles a
        INNER JOIN feeds f ON a.feedId = f.id
        WHERE (:showAll = 1 OR f.category IN (:categories) OR a.feedId IN (:feedIds))
        """
    )
    suspend fun getFilteredArticleCount(
        showAll: Int,
        categories: List<String>,
        feedIds: List<Long>,
    ): Int

    @Query("SELECT COUNT(*) FROM articles")
    fun observeTotalArticleCount(): Flow<Int>
}
