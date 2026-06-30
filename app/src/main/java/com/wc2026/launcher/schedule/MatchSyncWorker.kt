package com.wc2026.launcher.schedule

import android.content.Context
import androidx.work.*
import com.wc2026.launcher.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class MatchSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db   = MatchDatabase.getInstance(applicationContext)
        val repo = MatchScheduleRepo.getInstance(dao = db.matchDao())

        val result = repo.sync().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )

        // After a successful sync, (re-)schedule match-start alerts
        if (result == Result.success()) {
            scheduleMatchAlerts(db)
        }

        return result
    }

    private suspend fun scheduleMatchAlerts(db: MatchDatabase) {
        val settings = SettingsRepository.getInstance(applicationContext).settings.first()
        if (!settings.matchAlerts) return

        val now      = Instant.now()
        val nowIso   = now.toString()
        val upcoming = db.matchDao().getUpcomingMatches(nowIso)

        for (match in upcoming) {
            try {
                val kickOff   = ZonedDateTime.parse(match.utcDate).toInstant()
                val alertTime = kickOff.minusSeconds(15 * 60)
                val delayMs   = alertTime.toEpochMilli() - now.toEpochMilli()

                // Only schedule if the alert window is in the future and within 48 h
                if (delayMs > 0 && delayMs < 48 * 60 * 60 * 1000L) {
                    MatchAlertWorker.scheduleFor(applicationContext, match, delayMs)
                }
            } catch (_: Exception) {
                // Skip any match with a malformed date
            }
        }
    }

    companion object {
        private const val WORK_NAME = "wc2026_match_sync"

        /** Schedule a periodic sync every 5 minutes (WorkManager enforces a 15-min floor) */
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

        /** Trigger a one-off immediate sync (on app launch or boot) */
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
