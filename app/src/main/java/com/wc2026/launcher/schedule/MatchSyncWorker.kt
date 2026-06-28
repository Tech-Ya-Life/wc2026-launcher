package com.wc2026.launcher.schedule

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class MatchSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = MatchDatabase.getInstance(applicationContext)
        val repo = MatchScheduleRepo.getInstance(dao = db.matchDao())
        return repo.sync().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }

    companion object {
        private const val WORK_NAME = "wc2026_match_sync"

        /** Schedule a periodic sync every 5 minutes */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<MatchSyncWorker>(
                repeatInterval = 5,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /** Trigger a one-off immediate sync (e.g. on app launch) */
        fun syncNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<MatchSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
