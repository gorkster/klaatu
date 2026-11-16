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
        WHERE (:category IS NULL OR f.category = :category)
        ORDER BY a.publishedAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentArticles(category: String?, limit: Int = 50): List<ArticleWithFeed>

    @Query(
        """
        SELECT COUNT(*)
        FROM articles a
        INNER JOIN feeds f ON a.feedId = f.id
        WHERE (:category IS NULL OR f.category = :category)
        """
    )
    suspend fun getArticleCount(category: String?): Int

    @Query("SELECT COUNT(*) FROM articles")
    fun observeTotalArticleCount(): Flow<Int>
}
