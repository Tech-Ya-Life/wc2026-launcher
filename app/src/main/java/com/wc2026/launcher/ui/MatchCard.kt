package com.wc2026.launcher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.theme.LauncherTheme
import com.wc2026.launcher.theme.StarPlayers
import com.wc2026.launcher.theme.TeamFlags
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MatchCard(
    match: Match,
    theme: LauncherTheme,
    showScore: Boolean = true,
    isRecentResult: Boolean = false
) {
    val showActualScore = showScore && (match.isLive || match.isFinished)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.30f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header badge ───────────────────────────────
            when {
                match.isLive     -> LiveBadge()
                match.isFinished -> FullTimeBadge(match, theme.accent, isRecentResult)
                else             -> CountdownTimer(match.utcDate, theme.accent)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Main row: team ── score ── team ───────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home team
                TeamPanel(
                    modifier = Modifier.weight(1f),
                    tla = match.homeTeamTla,
                    name = match.homeTeamName,
                    isLive = match.isLive,
                    onBackground = theme.onBackground,
                    accentColor = theme.accent
                )

                // Centre: score or VS — animated between states
                ScoreOrVs(
                    showScore = showActualScore,
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    onBackground = theme.onBackground,
                    accentColor = theme.accent
                )

                // Away team
                TeamPanel(
                    modifier = Modifier.weight(1f),
                    tla = match.awayTeamTla,
                    name = match.awayTeamName,
                    isLive = match.isLive,
                    onBackground = theme.onBackground,
                    accentColor = theme.accent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Stage ──────────────────────────────────────
            Text(
                text = match.stage.replace("_", " ").lowercase()
                    .replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                color = theme.onBackground.copy(alpha = 0.45f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Team panel (flag + TLA + name + star player)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TeamPanel(
    modifier: Modifier = Modifier,
    tla: String,
    name: String,
    isLive: Boolean,
    onBackground: Color,
    accentColor: Color
) {
    val flag  = TeamFlags.forTla(tla)
    val star  = StarPlayers.forTla(tla)

    // Subtle scale pulse when live — flag "breathes" gently
    val infiniteTransition = rememberInfiniteTransition(label = "flagPulse_$tla")
    val flagScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLive) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            tween(1_600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "flagScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Flag emoji
        Text(
            text = flag,
            fontSize = 46.sp,
            modifier = Modifier.scale(flagScale),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // TLA (bold, prominent)
        Text(
            text = tla,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = onBackground,
            letterSpacing = 1.sp
        )

        // Full team name (small, dimmed)
        Text(
            text = name,
            fontSize = 10.sp,
            color = onBackground.copy(alpha = 0.55f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // Star player (accent, italic-ish)
        if (star.isNotBlank() && tla != "TBD") {
            Text(
                text = star,
                fontSize = 10.sp,
                color = accentColor.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Centre score / VS panel
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScoreOrVs(
    showScore: Boolean,
    homeScore: Int?,
    awayScore: Int?,
    onBackground: Color,
    accentColor: Color
) {
    // Key changes when the score actually changes → triggers fade animation
    val scoreKey = "$showScore|$homeScore|$awayScore"

    Box(
        modifier = Modifier.width(76.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = scoreKey,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
            label = "scoreAnim"
        ) { key ->
            val showing = key.startsWith("true")
            if (showing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${homeScore ?: "–"}",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = onBackground,
                        lineHeight = 40.sp
                    )
                    Text(
                        text = "—",
                        fontSize = 14.sp,
                        color = accentColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${awayScore ?: "–"}",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = onBackground,
                        lineHeight = 40.sp
                    )
                }
            } else {
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header badges
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LiveBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            tween(700, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE53935))
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "●",
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FullTimeBadge(match: Match, accentColor: Color, isRecentResult: Boolean) {
    val dateStr = runCatching {
        ZonedDateTime.parse(match.utcDate)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))
    }.getOrElse { "" }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isRecentResult && dateStr.isNotBlank()) {
            Text(
                text = "LAST RESULT  ·  $dateStr",
                fontSize = 10.sp,
                color = accentColor.copy(alpha = 0.55f),
                letterSpacing = 1.5.sp
            )
        }
        Text(
            text = "FULL TIME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor.copy(alpha = 0.85f),
            letterSpacing = 2.5.sp
        )
    }
}

@Composable
private fun CountdownTimer(utcDate: String, accentColor: Color) {
    var timeLeft by remember { mutableStateOf("") }

    LaunchedEffect(utcDate) {
        while (true) {
            val matchTime = ZonedDateTime.parse(utcDate)
            val now  = ZonedDateTime.now(ZoneId.of("UTC"))
            val diff = Duration.between(now, matchTime)

            timeLeft = when {
                diff.isNegative    -> "Starting soon"
                diff.toDays() > 0  -> "${diff.toDays()}d ${diff.toHours() % 24}h"
                diff.toHours() > 0 -> "${diff.toHours()}h ${diff.toMinutes() % 60}m"
                else               -> "${diff.toMinutes()}m ${diff.seconds % 60}s"
            }
            delay(1_000)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "NEXT MATCH IN",
            fontSize = 10.sp,
            color = accentColor.copy(alpha = 0.65f),
            letterSpacing = 2.sp
        )
        Text(
            text = timeLeft,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}
