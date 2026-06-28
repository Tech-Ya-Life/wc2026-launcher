package com.wc2026.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.wc2026.launcher.schedule.MatchSyncWorker
import com.wc2026.launcher.theme.LauncherTheme
import com.wc2026.launcher.ui.LauncherScreen

class LauncherActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display (draws behind status bar / nav bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Start background sync
        MatchSyncWorker.schedule(this)

        setContent {
            val theme by viewModel.theme.collectAsState()
            val nextMatch by viewModel.nextMatch.collectAsState()
            val allMatches by viewModel.allMatches.collectAsState()

            // Animate background gradient when theme changes
            val bgStart by animateColorAsState(
                targetValue = theme.backgroundStart,
                animationSpec = tween(durationMillis = 800),
                label = "bgStart"
            )
            val bgEnd by animateColorAsState(
                targetValue = theme.backgroundEnd,
                animationSpec = tween(durationMillis = 800),
                label = "bgEnd"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(bgStart, bgEnd))
                    )
            ) {
                LauncherScreen(
                    theme = theme,
                    nextMatch = nextMatch,
                    allMatches = allMatches
                )
            }
        }
    }
}
