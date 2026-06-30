package com.wc2026.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.theme.LauncherTheme

/**
 * Root composable for the launcher home screen.
 *
 * Layout (top → bottom):
 *   ┌──────────────────────────┐
 *   │  Status bar spacer        │
 *   │  Clock                    │
 *   │  Match card (next/live)   │
 *   │                           │
 *   │  App grid (4 × N)         │
 *   │                           │
 *   │  Dock (favourite apps)    │
 *   │  Nav bar spacer           │
 *   └──────────────────────────┘
 */
@Composable
fun LauncherScreen(
    theme: LauncherTheme,
    nextMatch: Match?,
    allMatches: List<Match>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Clock ──────────────────────────────────────
        LauncherClock(textColor = theme.onBackground)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Next Match Card ────────────────────────────
        if (nextMatch != null) {
            MatchCard(match = nextMatch, theme = theme)
        } else {
            NoMatchesCard(onBackground = theme.onBackground)
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── App Grid ───────────────────────────────────
        AppGrid(accentColor = theme.accent)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Dock ───────────────────────────────────────
        AppDock(accentColor = theme.accent)

        Spacer(modifier = Modifier.height(8.dp))
    }
}
