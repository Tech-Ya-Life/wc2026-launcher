package com.wc2026.launcher.theme

/**
 * Full player names for each TLA, used as the search query sent to TheSportsDB.
 * Abbreviated display names live in StarPlayers.kt; these are the exact search terms.
 */
object StarPlayerFullNames {
    val names: Map<String, String> = mapOf(
        // ── Hosts ──────────────────────────────────────
        "USA" to "Christian Pulisic",
        "CAN" to "Alphonso Davies",
        "MEX" to "Santiago Gimenez",

        // ── CONMEBOL ───────────────────────────────────
        "ARG" to "Lionel Messi",
        "BRA" to "Vinicius Junior",
        "COL" to "Luis Diaz",
        "ECU" to "Moises Caicedo",
        "PAR" to "Miguel Almiron",
        "URU" to "Darwin Nunez",

        // ── UEFA ────────────────────────────────────────
        "AUT" to "Marcel Sabitzer",
        "BEL" to "Kevin De Bruyne",
        "BIH" to "Edin Dzeko",
        "CRO" to "Luka Modric",
        "CZE" to "Tomas Soucek",
        "ENG" to "Jude Bellingham",
        "FRA" to "Kylian Mbappe",
        "GER" to "Florian Wirtz",
        "NED" to "Virgil van Dijk",
        "NOR" to "Erling Haaland",
        "POR" to "Cristiano Ronaldo",
        "SCO" to "Andrew Robertson",
        "ESP" to "Pedri",
        "SWE" to "Dejan Kulusevski",
        "SUI" to "Granit Xhaka",
        "TUR" to "Hakan Calhanoglu",

        // ── CAF ─────────────────────────────────────────
        "ALG" to "Riyad Mahrez",
        "CPV" to "Pepe",
        "COD" to "Yannick Carrasco",
        "CIV" to "Sebastien Haller",
        "EGY" to "Mohamed Salah",
        "GHA" to "Mohammed Kudus",
        "MAR" to "Achraf Hakimi",
        "SEN" to "Sadio Mane",
        "RSA" to "Percy Tau",
        "TUN" to "Hannibal Mejbri",

        // ── AFC ─────────────────────────────────────────
        "AUS" to "Matthew Leckie",
        "IRN" to "Mehdi Taremi",
        "JPN" to "Takefusa Kubo",
        "KOR" to "Son Heung-min",
        "QAT" to "Akram Afif",
        "KSA" to "Salem Al-Dawsari",
        "IRQ" to "Aymen Hussein",
        "JOR" to "Yazan Al-Naimat",
        "UZB" to "Jasurbek Yakhshiboev",

        // ── OFC ─────────────────────────────────────────
        "NZL" to "Chris Wood",

        // ── CONCACAF (non-hosts) ─────────────────────────
        "CUW" to "Leandro Bacuna",
        "HAI" to "Frantzdy Pierrot",
        "PAN" to "Andres Andrade",
    )

    fun forTla(tla: String): String? = names[tla]
}
