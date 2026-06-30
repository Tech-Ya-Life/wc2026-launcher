package com.wc2026.launcher

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.wc2026.launcher.schedule.MatchAlertWorker
import com.wc2026.launcher.schedule.MatchSyncWorker
import com.wc2026.launcher.ui.FootballPitchOverlay
import com.wc2026.launcher.ui.LauncherScreen

class LauncherActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    // Android 13+ notification permission launcher
    private val notificationPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: we just ask once; the setting still works regardless */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        MatchSyncWorker.schedule(this)

        setContent {
            val theme       by viewModel.theme.collectAsState()
            val nextMatch   by viewModel.nextMatch.collectAsState()
            val lastMatch   by viewModel.lastMatch.collectAsState()
            val allMatches  by viewModel.allMatches.collectAsState()
            val appSettings by viewModel.appSettings.collectAsState()
            val standings   by viewModel.standings.collectAsState()

            val bgStart by animateColorAsState(
                targetValue  = theme.backgroundStart,
                animationSpec = tween(durationMillis = 800),
                label        = "bgStart"
            )
            val bgEnd by animateColorAsState(
                targetValue  = theme.backgroundEnd,
                animationSpec = tween(durationMillis = 800),
                label        = "bgEnd"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(bgStart, bgEnd)))
            ) {
                FootballPitchOverlay()

                LauncherScreen(
                    theme          = theme,
                    nextMatch      = nextMatch,
                    lastMatch      = lastMatch,
                    allMatches     = allMatches,
                    showLiveScores = appSettings.showLiveScores,
                    standings      = standings,
                    showStandings  = appSettings.showStandings
                )
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        val nm = getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(MatchAlertWorker.CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            MatchAlertWorker.CHANNEL_ID,
            "Match Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies 15 minutes before each World Cup match kicks off"
            enableVibration(true)
        }
        nm.createNotificationChannel(channel)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) return
        notificationPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
