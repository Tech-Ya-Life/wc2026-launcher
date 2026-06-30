package com.wc2026.launcher.theme

import androidx.compose.ui.graphics.Color
import com.wc2026.launcher.schedule.Match

/**
 * Derives the active launcher theme from the current match.
 *
 * For a match between Team A (home) and Team B (away):
 *  - backgroundStart = Team A primary   (left / top)
 *  - backgroundEnd   = Team B primary   (right / bottom)
 *  - accent          = blend of both secondaries
 *  - onBackground    = whichever of white/black reads best
 */
data class LauncherTheme(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val accent: Color,
    val onBackground: Color,
    val homeTeamTla: String,
    val awayTeamTla: String
)

object ThemeEngine {

    val default = LauncherTheme(
        backgroundStart = Color(0xFF0D47A1),
        backgroundEnd   = Color(0xFF1B5E20),
        accent          = Color(0xFFFFD700),
        onBackground    = Color.White,
        homeTeamTla     = "WC",
        awayTeamTla     = "2026"
    )

    /**
     * Pins the theme to a single team — home = away = that team's palette,
     * giving a solid block of the team's primary colour.
     */
    fun fromTeam(tla: String): LauncherTheme {
        val palette = TeamColorPalette.forTeam(tla)
        val bg = lerp(palette.primary, palette.secondary, 0.3f)
        val onBg = if (luminance(bg) > 0.4f) Color.Black else Color.White
        return LauncherTheme(
            backgroundStart = palette.primary,
            backgroundEnd   = palette.secondary,
            accent          = palette.secondary,
            onBackground    = onBg,
            homeTeamTla     = tla,
            awayTeamTla     = tla
        )
    }

    fun fromMatch(match: Match): LauncherTheme {
        val home = TeamColorPalette.forTeam(match.homeTeamTla)
        val away = TeamColorPalette.forTeam(match.awayTeamTla)

        val accent = lerp(home.secondary, away.secondary, 0.5f)
        val bg = lerp(home.primary, away.primary, 0.5f)
        val onBg = if (luminance(bg) > 0.4f) Color.Black else Color.White

        return LauncherTheme(
            backgroundStart = home.primary,
            backgroundEnd   = away.primary,
            accent          = accent,
            onBackground    = onBg,
            homeTeamTla     = match.homeTeamTla,
            awayTeamTla     = match.awayTeamTla
        )
    }

    // ── Color math helpers ────────────────────────────────

    private fun lerp(a: Color, b: Color, t: Float) = Color(
        red   = a.red   + (b.red   - a.red)   * t,
        green = a.green + (b.green - a.green) * t,
        blue  = a.blue  + (b.blue  - a.blue)  * t,
        alpha = 1f
    )

    /** Relative luminance (WCAG formula) */
    private fun luminance(c: Color): Float {
        fun channel(v: Float) = if (v <= 0.03928f) v / 12.92f else ((v + 0.055f) / 1.055f).let { it * it }
        return 0.2126f * channel(c.red) + 0.7152f * channel(c.green) + 0.0722f * channel(c.blue)
    }
}
