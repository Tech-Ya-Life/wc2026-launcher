package com.wc2026.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.schedule.Standing
import com.wc2026.launcher.theme.LauncherTheme

/**
 * Root composable for the launcher home screen.
 *
 * Layout (top → bottom):
 *   ┌──────────────────────────┐
 *   │  Status bar spacer        │
 *   │  Clock                    │
 *   │  Match card               │  ← next/live, or last result, or empty state
 *   │  Standings widget (opt.)  │
 *   │  ── flex spacer ──        │
 *   │  App grid (4 × N)         │
 *   │  Dock (favourite apps)    │
 *   │  Nav bar spacer           │
 *   └──────────────────────────┘
 */
@Composable
fun LauncherScreen(
    theme: LauncherTheme,
    nextMatch: Match?,
    lastMatch: Match? = null,
    allMatches: List<Match>,
    showLiveScores: Boolean = true,
    standings: List<Standing> = emptyList(),
    showStandings: Boolean = false,
    playerImages: Map<String, String> = emptyMap()
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

        // ── Match Card ─────────────────────────────────
        when {
            nextMatch != null ->
                MatchCard(
                    match = nextMatch,
                    theme = theme,
                    showScore = showLiveScores,
                    playerImages = playerImages
                )

            lastMatch != null ->
                MatchCard(
                    match = lastMatch,
                    theme = theme,
                    showScore = showLiveScores,
                    isRecentResult = true,
                    playerImages = playerImages
                )

            else ->
                NoMatchesCard(onBackground = theme.onBackground)
        }

        // ── Group Standings (optional) ─────────────────
        if (showStandings && standings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            StandingsWidget(
                groups = standings,
                accentColor = theme.accent,
                onBackground = theme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
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
