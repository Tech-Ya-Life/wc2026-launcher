package com.wc2026.launcher.theme

/**
 * A featured player name for each of the 48 WC2026 qualified teams.
 * Shown beneath the team name in the match card for a bit of personality.
 */
object StarPlayers {

    private val players: Map<String, String> = mapOf(
        // ── Hosts ──────────────────────────────────────
        "USA" to "C. Pulisic",
        "CAN" to "A. Davies",
        "MEX" to "S. Giménez",

        // ── CONMEBOL ───────────────────────────────────
        "ARG" to "L. Messi",
        "BRA" to "Vinicius Jr.",
        "COL" to "L. Díaz",
        "ECU" to "M. Caicedo",
        "PAR" to "M. Almirón",
        "URU" to "D. Núñez",

        // ── UEFA ────────────────────────────────────────
        "AUT" to "M. Sabitzer",
        "BEL" to "K. De Bruyne",
        "BIH" to "E. Džeko",
        "CRO" to "L. Modrić",
        "CZE" to "T. Souček",
        "ENG" to "J. Bellingham",
        "FRA" to "K. Mbappé",
        "GER" to "F. Wirtz",
        "NED" to "V. van Dijk",
        "NOR" to "E. Haaland",
        "POR" to "C. Ronaldo",
        "SCO" to "A. Robertson",
        "ESP" to "Pedri",
        "SWE" to "D. Kulusevski",
        "SUI" to "G. Xhaka",
        "TUR" to "H. Çalhanoğlu",

        // ── CAF ─────────────────────────────────────────
        "ALG" to "R. Mahrez",
        "CPV" to "Pepê",
        "COD" to "Y. Carrasco",
        "CIV" to "S. Haller",
        "EGY" to "M. Salah",
        "GHA" to "M. Kudus",
        "MAR" to "A. Hakimi",
        "SEN" to "S. Mané",
        "RSA" to "P. Tau",
        "TUN" to "H. Mejbri",

        // ── AFC ─────────────────────────────────────────
        "AUS" to "M. Leckie",
        "IRQ" to "A. Hussein",
        "IRN" to "M. Taremi",
        "JPN" to "T. Kubo",
        "JOR" to "Y. Al-Naimat",
        "KOR" to "Son Heung-min",
        "QAT" to "A. Afif",
        "KSA" to "S. Al-Dawsari",
        "UZB" to "J. Yakhshiboev",

        // ── OFC ─────────────────────────────────────────
        "NZL" to "C. Wood",

        // ── CONCACAF (non-hosts) ─────────────────────────
        "CUW" to "L. Bacuna",
        "HAI" to "F. Pierrot",
        "PAN" to "A. Andrade",
    )

    /** Returns a featured player name for a TLA, or empty string if unknown */
    fun forTla(tla: String): String = players[tla] ?: ""
}
