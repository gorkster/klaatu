package com.klaatu.rss.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FeedEntity::class, ArticleEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class KlaatuDatabase : RoomDatabase() {
    abstract fun dao(): KlaatuDao

    companion object {
        @Volatile
        private var instance: KlaatuDatabase? = null

        fun getInstance(context: Context): KlaatuDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KlaatuDatabase::class.java,
                    "klaatu.db",
                ).build().also { instance = it }
            }
    }
}
