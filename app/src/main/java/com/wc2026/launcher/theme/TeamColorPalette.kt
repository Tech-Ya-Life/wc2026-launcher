package com.wc2026.launcher.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palettes for all 48 World Cup 2026 teams.
 * Each team has a primary color (home kit) and secondary color (away kit / accent).
 */
data class TeamPalette(
    val tla: String,            // 3-letter code matching API
    val name: String,
    val primary: Color,
    val secondary: Color,
    val onPrimary: Color = Color.White
)

object TeamColorPalette {

    val palettes: Map<String, TeamPalette> = listOf(

        // ── GROUP A (USA host group) ──
        TeamPalette("USA", "United States",  Color(0xFF002868), Color(0xFFBF0A30)),
        TeamPalette("CAN", "Canada",         Color(0xFFFF0000), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("MEX", "Mexico",         Color(0xFF006847), Color(0xFFCE1126)),

        // ── SOUTH AMERICA ──
        TeamPalette("BRA", "Brazil",         Color(0xFF009C3B), Color(0xFFFFDF00), Color.Black),
        TeamPalette("ARG", "Argentina",      Color(0xFF74ACDF), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("URU", "Uruguay",        Color(0xFF5FFFFF), Color(0xFF002366), Color.Black),
        TeamPalette("COL", "Colombia",       Color(0xFFFCD116), Color(0xFF003087), Color.Black),
        TeamPalette("ECU", "Ecuador",        Color(0xFFFFD100), Color(0xFF003087), Color.Black),
        TeamPalette("CHI", "Chile",          Color(0xFFD52B1E), Color(0xFF002B7F)),
        TeamPalette("VEN", "Venezuela",      Color(0xFFCC0001), Color(0xFF003082)),
        TeamPalette("PAR", "Paraguay",       Color(0xFFD52B1E), Color(0xFF002B7F)),
        TeamPalette("BOL", "Bolivia",        Color(0xFFD52B1E), Color(0xFF007A33)),
        TeamPalette("PER", "Peru",           Color(0xFFD91023), Color(0xFFFFFFFF), Color.Black),

        // ── EUROPE ──
        TeamPalette("ENG", "England",        Color(0xFFFFFFFF), Color(0xFF003090), Color.Black),
        TeamPalette("FRA", "France",         Color(0xFF002395), Color(0xFFED2939)),
        TeamPalette("GER", "Germany",        Color(0xFFFFFFFF), Color(0xFF000000), Color.Black),
        TeamPalette("ESP", "Spain",          Color(0xFFC60B1E), Color(0xFFFFC400)),
        TeamPalette("POR", "Portugal",       Color(0xFF006600), Color(0xFFFF0000)),
        TeamPalette("NED", "Netherlands",    Color(0xFFFF6600), Color(0xFF002B7F)),
        TeamPalette("BEL", "Belgium",        Color(0xFFEF3340), Color(0xFF000000)),
        TeamPalette("ITA", "Italy",          Color(0xFF009246), Color(0xFF003082)),
        TeamPalette("CRO", "Croatia",        Color(0xFFFF0000), Color(0xFF0000FF)),
        TeamPalette("DEN", "Denmark",        Color(0xFFC60C30), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("SWI", "Switzerland",    Color(0xFFFF0000), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("AUT", "Austria",        Color(0xFFED2939), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("POL", "Poland",         Color(0xFFFFFFFF), Color(0xFFDC143C), Color.Black),
        TeamPalette("SRB", "Serbia",         Color(0xFFC6363C), Color(0xFF0C4076)),
        TeamPalette("SCO", "Scotland",       Color(0xFF003399), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("TUR", "Turkey",         Color(0xFFE30A17), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("UKR", "Ukraine",        Color(0xFF005BBB), Color(0xFFFFD500), Color.Black),
        TeamPalette("SVK", "Slovakia",       Color(0xFF003082), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("HUN", "Hungary",        Color(0xFFCE2939), Color(0xFF477050)),
        TeamPalette("ROU", "Romania",        Color(0xFF002B7F), Color(0xFFFCD116), Color.Black),
        TeamPalette("WAL", "Wales",          Color(0xFFD01012), Color(0xFF00AB39)),

        // ── AFRICA ──
        TeamPalette("MAR", "Morocco",        Color(0xFFC1272D), Color(0xFF006233)),
        TeamPalette("SEN", "Senegal",        Color(0xFF00853F), Color(0xFFFCDD09), Color.Black),
        TeamPalette("CMR", "Cameroon",       Color(0xFF007A5E), Color(0xFFCE1126)),
        TeamPalette("GHA", "Ghana",          Color(0xFF006B3F), Color(0xFFFCD116), Color.Black),
        TeamPalette("NGA", "Nigeria",        Color(0xFF008751), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("CGO", "D.R. Congo",     Color(0xFF007FFF), Color(0xFFCE1126)),
        TeamPalette("CIV", "Côte d'Ivoire", Color(0xFFF77F00), Color(0xFF009A44)),
        TeamPalette("EGY", "Egypt",          Color(0xFFCE1126), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("ALG", "Algeria",        Color(0xFF006233), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("TUN", "Tunisia",        Color(0xFFE70013), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("MLI", "Mali",           Color(0xFF14B53A), Color(0xFFFCD116), Color.Black),
        TeamPalette("UGA", "Uganda",         Color(0xFF000000), Color(0xFFFFCC00), Color.Black),

        // ── ASIA / OCEANIA ──
        TeamPalette("JPN", "Japan",          Color(0xFF003087), Color(0xFFBC002D)),
        TeamPalette("KOR", "South Korea",    Color(0xFFCD2E3A), Color(0xFF003478)),
        TeamPalette("AUS", "Australia",      Color(0xFF00843D), Color(0xFFFFD100), Color.Black),
        TeamPalette("IRN", "Iran",           Color(0xFF239F40), Color(0xFFDA0000)),
        TeamPalette("SAU", "Saudi Arabia",   Color(0xFF006C35), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("QAT", "Qatar",          Color(0xFF8D1B3D), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("UAE", "UAE",            Color(0xFF00732F), Color(0xFFFF0000)),
        TeamPalette("CHN", "China",          Color(0xFFDE2910), Color(0xFFFFDE00), Color.Black),
        TeamPalette("NZL", "New Zealand",    Color(0xFF000000), Color(0xFFFFFFFF), Color.Black),

        // ── CONCACAF ──
        TeamPalette("PAN", "Panama",         Color(0xFFD21034), Color(0xFF003893)),
        TeamPalette("CRC", "Costa Rica",     Color(0xFF002B7F), Color(0xFFCE1126)),
        TeamPalette("JAM", "Jamaica",        Color(0xFF000000), Color(0xFFFFD700), Color.Black),
        TeamPalette("HON", "Honduras",       Color(0xFF0073CF), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("GUA", "Guatemala",      Color(0xFF4997D0), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("ELS", "El Salvador",    Color(0xFF0F47AF), Color(0xFFFFFFFF), Color.Black),
        TeamPalette("TRI", "Trinidad & Tobago", Color(0xFFCE1126), Color(0xFF000000)),

    ).associateBy { it.tla }

    /** Fallback for teams not in the list */
    val default = TeamPalette("???", "Unknown", Color(0xFF1A237E), Color(0xFF283593))

    fun forTeam(tla: String): TeamPalette = palettes[tla] ?: default
}
