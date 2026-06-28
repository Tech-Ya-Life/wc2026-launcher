package com.wc2026.launcher.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-schedules background sync after the device reboots */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MatchSyncWorker.schedule(context)
        }
    }
}
