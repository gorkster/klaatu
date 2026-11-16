package com.klaatu.rss.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.klaatu.rss.data.FeedRepository
import com.klaatu.rss.widget.KlaatuWidget
import java.util.concurrent.TimeUnit

class FeedSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = FeedRepository(applicationContext)
        repo.syncAllFeeds()
        KlaatuWidget().updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "klaatu_periodic_feed_sync"

        fun schedulePeriodic(context: Context, intervalMinutes: Long = 30L) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<FeedSyncWorker>(
                intervalMinutes.coerceAtLeast(15L),
                TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        fun enqueueImmediate(context: Context) {
            val request = OneTimeWorkRequestBuilder<FeedSyncWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
