package com.wc2026.launcher.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.wc2026.launcher.R
import com.wc2026.launcher.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/**
 * Fires a "kick-off in 15 minutes" notification for a specific match.
 * Scheduled by [MatchSyncWorker] with an initial delay so it fires at
 * (matchTime − 15 min). Silently exits if the user has turned alerts off.
 */
class MatchAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Honour the user setting — they may have turned alerts off since scheduling
        val settings = SettingsRepository.getInstance(applicationContext).settings.first()
        if (!settings.matchAlerts) return Result.success()

        val homeTeam = inputData.getString(KEY_HOME) ?: return Result.success()
        val awayTeam = inputData.getString(KEY_AWAY) ?: return Result.success()
        val matchId  = inputData.getInt(KEY_MATCH_ID, 0)

        ensureChannel()

        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_football)
            .setContentTitle("⚽ Kick-off in 15 minutes!")
            .setContentText("$homeTeam  vs  $awayTeam")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$homeTeam  vs  $awayTeam\nOpen the launcher to follow live."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(matchId, notification)
        return Result.success()
    }

    private fun ensureChannel() {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Match Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies 15 minutes before each World Cup match kicks off"
            enableVibration(true)
        }
        nm.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID    = "wc2026_match_alerts"
        const val KEY_HOME      = "home_team"
        const val KEY_AWAY      = "away_team"
        const val KEY_MATCH_ID  = "match_id"

        /**
         * Schedule a one-time alert for [match] to fire [delayMs] milliseconds from now.
         * Uses a unique work name so re-scheduling the same match doesn't double-notify.
         */
        fun scheduleFor(context: Context, match: Match, delayMs: Long) {
            val data = workDataOf(
                KEY_HOME     to match.homeTeamName,
                KEY_AWAY     to match.awayTeamName,
                KEY_MATCH_ID to match.id
            )
            val request = OneTimeWorkRequestBuilder<MatchAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .addTag("match_alert")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "alert_${match.id}",
                ExistingWorkPolicy.KEEP, // don't reschedule if already queued
                request
            )
        }
    }
}
