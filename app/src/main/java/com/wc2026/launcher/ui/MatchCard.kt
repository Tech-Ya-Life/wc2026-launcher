package com.wc2026.launcher.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
    isRecentResult: Boolean = false,
    playerImages: Map<String, String> = emptyMap()
) {
    val showActualScore = showScore && (match.isLive || match.isFinished)

    val homeImageUrl = playerImages[match.homeTeamTla]
    val awayImageUrl = playerImages[match.awayTeamTla]
    val hasCutouts   = homeImageUrl != null || awayImageUrl != null

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
            if (hasCutouts) {
                // When player images are available: cutout figures frame the score
                PlayerCutoutRow(
                    match          = match,
                    showActualScore = showActualScore,
                    homeImageUrl   = homeImageUrl,
                    awayImageUrl   = awayImageUrl,
                    onBackground   = theme.onBackground,
                    accentColor    = theme.accent
                )
            } else {
                // Fallback: flag-emoji panels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamPanel(
                        modifier = Modifier.weight(1f),
                        tla = match.homeTeamTla,
                        name = match.homeTeamName,
                        isLive = match.isLive,
                        onBackground = theme.onBackground,
                        accentColor = theme.accent
                    )
                    ScoreOrVs(
                        showScore = showActualScore,
                        homeScore = match.homeScore,
                        awayScore = match.awayScore,
                        onBackground = theme.onBackground,
                        accentColor = theme.accent
                    )
                    TeamPanel(
                        modifier = Modifier.weight(1f),
                        tla = match.awayTeamTla,
                        name = match.awayTeamName,
                        isLive = match.isLive,
                        onBackground = theme.onBackground,
                        accentColor = theme.accent
                    )
                }
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
//  Player cutout row — shown when we have real player images
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlayerCutoutRow(
    match: Match,
    showActualScore: Boolean,
    homeImageUrl: String?,
    awayImageUrl: String?,
    onBackground: Color,
    accentColor: Color
) {
    val homeFlag = TeamFlags.forTla(match.homeTeamTla)
    val awayFlag = TeamFlags.forTla(match.awayTeamTla)
    val homeStar = StarPlayers.forTla(match.homeTeamTla)
    val awayStar = StarPlayers.forTla(match.awayTeamTla)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom  // align to bottom so player images "stand" on the same baseline
    ) {
        // ── Home player ───────────────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerFigure(
                imageUrl    = homeImageUrl,
                flagEmoji   = homeFlag,
                isLive      = match.isLive,
                isCutout    = homeImageUrl?.contains("/cutout/") == true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = match.homeTeamTla,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = onBackground,
                letterSpacing = 1.sp
            )
            if (homeStar.isNotBlank() && match.homeTeamTla != "TBD") {
                Text(
                    text = homeStar,
                    fontSize = 9.sp,
                    color = accentColor.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ── Score / VS ────────────────────────────────────
        Column(
            modifier = Modifier
                .width(76.dp)
                .padding(bottom = 20.dp),   // offset up from team-name baseline
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreOrVs(
                showScore    = showActualScore,
                homeScore    = match.homeScore,
                awayScore    = match.awayScore,
                onBackground = onBackground,
                accentColor  = accentColor
            )
        }

        // ── Away player ───────────────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerFigure(
                imageUrl    = awayImageUrl,
                flagEmoji   = awayFlag,
                isLive      = match.isLive,
                isCutout    = awayImageUrl?.contains("/cutout/") == true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = match.awayTeamTla,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = onBackground,
                letterSpacing = 1.sp
            )
            if (awayStar.isNotBlank() && match.awayTeamTla != "TBD") {
                Text(
                    text = awayStar,
                    fontSize = 9.sp,
                    color = accentColor.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * A player "figure" — either:
 *  • A transparent-background cutout shown at full height (tall portrait, no clip)
 *  • A regular headshot shown as a circular 52dp portrait
 *  • The flag emoji as a fallback while loading or when no image is found
 *
 * The [isCutout] flag signals that the URL points to a PNG with transparency
 * (the player floats on the card background without a circle mask).
 */
@Composable
private fun PlayerFigure(
    imageUrl: String?,
    flagEmoji: String,
    isLive: Boolean,
    isCutout: Boolean
) {
    // Subtle scale pulse when live
    val infiniteTransition = rememberInfiniteTransition(label = "playerPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (isLive) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            tween(1_800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "playerScale"
    )

    Crossfade(
        targetState = imageUrl,
        animationSpec = tween(600),
        label = "playerImageFade"
    ) { url ->
        if (url != null) {
            if (isCutout) {
                // Transparent-background cutout — no circular clip, player "floats"
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(80.dp)
                        .height(108.dp)
                        .scale(scale)
                )
            } else {
                // Regular thumb — circular crop
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .scale(scale)
                    )
                    // Tiny flag badge at bottom-right corner of portrait
                    Text(
                        text = flagEmoji,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .padding(2.dp)
                    )
                }
            }
        } else {
            // Loading / no image — show flag emoji
            Text(
                text = flagEmoji,
                fontSize = 48.sp,
                modifier = Modifier.scale(scale)
            )
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
