package com.wc2026.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.theme.LauncherTheme
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MatchCard(
    match: Match,
    theme: LauncherTheme,
    showScore: Boolean = true,
    isRecentResult: Boolean = false   // true when showing last finished match
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Header row: LIVE badge / FULL TIME / countdown
            when {
                match.isLive     -> LiveBadge()
                match.isFinished -> FullTimeBadge(match, theme.accent, isRecentResult)
                else             -> CountdownTimer(utcDate = match.utcDate, accentColor = theme.accent)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams + scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val showActualScore = showScore && (match.isLive || match.isFinished)

                TeamBlock(
                    name  = match.homeTeamName,
                    tla   = match.homeTeamTla,
                    score = if (showActualScore) match.homeScore?.toString() else null,
                    color = theme.onBackground
                )

                Text(
                    text      = if (showActualScore) "–" else "VS",
                    fontSize  = if (showActualScore) 28.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color     = theme.accent
                )

                TeamBlock(
                    name  = match.awayTeamName,
                    tla   = match.awayTeamTla,
                    score = if (showActualScore) match.awayScore?.toString() else null,
                    color = theme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text  = match.stage.replace("_", " ").lowercase()
                    .replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                color = theme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun TeamBlock(name: String, tla: String, score: String?, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(110.dp)) {
        Text(text = tla, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = name, fontSize = 11.sp, color = color.copy(alpha = 0.7f), maxLines = 1)
        if (score != null) {
            Text(text = score, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun LiveBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFE53935))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text("● LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun FullTimeBadge(match: Match, accentColor: Color, isRecentResult: Boolean) {
    // Parse the match date for display
    val dateStr = runCatching {
        ZonedDateTime.parse(match.utcDate)
            .withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))
    }.getOrElse { "" }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isRecentResult) {
            Text(
                text = "LAST RESULT  ·  $dateStr",
                fontSize = 10.sp,
                color = accentColor.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            )
        }
        Text(
            text = "FULL TIME",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor.copy(alpha = 0.85f),
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun CountdownTimer(utcDate: String, accentColor: Color) {
    var timeLeft by remember { mutableStateOf("") }

    LaunchedEffect(utcDate) {
        while (true) {
            val matchTime = ZonedDateTime.parse(utcDate)
            val now = ZonedDateTime.now(ZoneId.of("UTC"))
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
        Text("NEXT MATCH IN", fontSize = 10.sp, color = accentColor.copy(alpha = 0.7f), letterSpacing = 2.sp)
        Text(timeLeft, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accentColor)
    }
}
