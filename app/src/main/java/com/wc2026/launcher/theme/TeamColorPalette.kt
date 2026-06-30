package com.wc2026.launcher.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palettes for all 48 FIFA World Cup 2026 qualified teams.
 * Hosts: USA, Canada, Mexico
 * Source: https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/teams
 */
data class TeamPalette(
    val tla: String,           // 3-letter code matching football-data.org API
    val name: String,
    val primary: Color,
    val secondary: Color,
    val onPrimary: Color = Color.White
)

object TeamColorPalette {

    val palettes: Map<String, TeamPalette> = listOf(

        // ── HOST NATIONS ──────────────────────────────────────────────────────
        TeamPalette("USA", "United States",  Color(0xFF002868), Color(0xFFBF0A30)),
        TeamPalette("CAN", "Canada",         Color(0xFFFF0000), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("MEX", "Mexico",         Color(0xFF006847), Color(0xFFCE1126)),

        // ── CONMEBOL (6 qualifiers) ───────────────────────────────────────────
        TeamPalette("ARG", "Argentina",      Color(0xFF74ACDF), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("BRA", "Brazil",         Color(0xFF009C3B), Color(0xFFFFDF00), Color.Black),
        TeamPalette("COL", "Colombia",       Color(0xFFFCD116), Color(0xFF003087), Color.Black),
        TeamPalette("ECU", "Ecuador",        Color(0xFFFFD100), Color(0xFF003087), Color.Black),
        TeamPalette("PAR", "Paraguay",       Color(0xFFD52B1E), Color(0xFF002B7F)),
        TeamPalette("URU", "Uruguay",        Color(0xFF5BB8E8), Color(0xFF002366), Color.Black),

        // ── UEFA (16 qualifiers) ─────────────────────────────────────────────
        TeamPalette("AUT", "Austria",        Color(0xFFED2939), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("BEL", "Belgium",        Color(0xFFEF3340), Color(0xFF000000)),
        TeamPalette("BIH", "Bosnia & Herz.", Color(0xFF002395), Color(0xFFFFCD00), Color.Black),
        TeamPalette("CRO", "Croatia",        Color(0xFFFF0000), Color(0xFF003399)),
        TeamPalette("CZE", "Czechia",        Color(0xFFD7141A), Color(0xFF11457E)),
        TeamPalette("ENG", "England",        Color(0xFFFFFFFF), Color(0xFF003090), Color.Black),
        TeamPalette("FRA", "France",         Color(0xFF002395), Color(0xFFED2939)),
        TeamPalette("GER", "Germany",        Color(0xFFFFFFFF), Color(0xFF000000), Color.Black),
        TeamPalette("NED", "Netherlands",    Color(0xFFFF6600), Color(0xFF002B7F)),
        TeamPalette("NOR", "Norway",         Color(0xFFEF2B2D), Color(0xFF002868)),
        TeamPalette("POR", "Portugal",       Color(0xFF006600), Color(0xFFFF0000)),
        TeamPalette("SCO", "Scotland",       Color(0xFF003399), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("ESP", "Spain",          Color(0xFFC60B1E), Color(0xFFFFC400)),
        TeamPalette("SWE", "Sweden",         Color(0xFF006AA7), Color(0xFFFECC02), Color.Black),
        TeamPalette("SUI", "Switzerland",    Color(0xFFFF0000), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("TUR", "Türkiye",        Color(0xFFE30A17), Color(0xFFFFFFFF), Color.Black),

        // ── CAF (10 qualifiers) ───────────────────────────────────────────────
        TeamPalette("ALG", "Algeria",        Color(0xFF006233), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("CPV", "Cabo Verde",     Color(0xFF003893), Color(0xFFCF2027)),
        TeamPalette("COD", "Congo DR",       Color(0xFF007FFF), Color(0xFFCE1126)),
        TeamPalette("CIV", "Côte d'Ivoire", Color(0xFFF77F00), Color(0xFF009A44)),
        TeamPalette("EGY", "Egypt",          Color(0xFFCE1126), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("GHA", "Ghana",          Color(0xFF006B3F), Color(0xFFFCD116), Color.Black),
        TeamPalette("MAR", "Morocco",        Color(0xFFC1272D), Color(0xFF006233)),
        TeamPalette("SEN", "Senegal",        Color(0xFF00853F), Color(0xFFFCDD09), Color.Black),
        TeamPalette("RSA", "South Africa",   Color(0xFF007A4D), Color(0xFFFFB81C), Color.Black),
        TeamPalette("TUN", "Tunisia",        Color(0xFFE70013), Color(0xFFFFFFFF), Color.Black),

        // ── AFC (9 qualifiers) ────────────────────────────────────────────────
        TeamPalette("AUS", "Australia",      Color(0xFF00843D), Color(0xFFFFD100), Color.Black),
        TeamPalette("IRQ", "Iraq",           Color(0xFF000000), Color(0xFF007A3D)),
        TeamPalette("IRN", "Iran",           Color(0xFF239F40), Color(0xFFDA0000)),
        TeamPalette("JPN", "Japan",          Color(0xFF003087), Color(0xFFBC002D)),
        TeamPalette("JOR", "Jordan",         Color(0xFF007A3D), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("KOR", "South Korea",    Color(0xFFCD2E3A), Color(0xFF003478)),
        TeamPalette("QAT", "Qatar",          Color(0xFF8D1B3D), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("KSA", "Saudi Arabia",   Color(0xFF006C35), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("UZB", "Uzbekistan",     Color(0xFF1EB53A), Color(0xFF0099B5)),

        // ── OFC (1 qualifier) ─────────────────────────────────────────────────
        TeamPalette("NZL", "New Zealand",    Color(0xFF000000), Color(0xFFFFFFFF), Color.Black),

        // ── CONCACAF (3 non-host qualifiers) ─────────────────────────────────
        TeamPalette("CUW", "Curaçao",        Color(0xFF002B7F), Color(0xFF00A3E0)),
        TeamPalette("HAI", "Haiti",          Color(0xFF00209F), Color(0xFFD21034)),
        TeamPalette("PAN", "Panama",         Color(0xFFD21034), Color(0xFF003893)),

    ).associateBy { it.tla }

    /** Fallback for teams not in the list */
    val default = TeamPalette("???", "Unknown", Color(0xFF1A237E), Color(0xFF283593))

    fun forTeam(tla: String): TeamPalette = palettes[tla] ?: default
}
