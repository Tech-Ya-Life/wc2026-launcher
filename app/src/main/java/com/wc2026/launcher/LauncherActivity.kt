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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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

            // Slowly sweep the gradient angle back and forth — a living background
            val infiniteTransition = rememberInfiniteTransition(label = "bgDrift")
            val gradPhase by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue  = 1f,
                animationSpec = infiniteRepeatable(
                    tween(12_000, easing = LinearEasing),
                    RepeatMode.Reverse
                ),
                label = "gradPhase"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Diagonal sweeps from top-left↘ to top-right↙ and back
                        val sweep = size.width * 0.35f * gradPhase
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(bgStart, bgEnd),
                                start  = Offset(sweep, 0f),
                                end    = Offset(size.width - sweep, size.height)
                            ),
                            size = size
                        )
                    }
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
