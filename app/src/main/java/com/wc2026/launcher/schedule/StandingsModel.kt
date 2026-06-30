package com.wc2026.launcher.schedule

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * football-data.org v4  GET /v4/competitions/WC/standings
 *
 * Standings are split by stage (GROUP_STAGE / LAST_16 etc.) and type
 * (TOTAL / HOME / AWAY). We only care about entries that have a group label.
 */
@JsonClass(generateAdapter = true)
data class StandingsResponse(
    @Json(name = "standings") val standings: List<Standing>
)

@JsonClass(generateAdapter = true)
data class Standing(
    @Json(name = "stage") val stage: String,       // "GROUP_STAGE", "LAST_16", etc.
    @Json(name = "type") val type: String,          // "TOTAL", "HOME", "AWAY"
    @Json(name = "group") val group: String?,       // "GROUP_A" … "GROUP_L"  (null for knockout)
    @Json(name = "table") val table: List<TableEntry>
)

@JsonClass(generateAdapter = true)
data class TableEntry(
    @Json(name = "position") val position: Int,
    @Json(name = "team") val team: StandingTeam,
    @Json(name = "playedGames") val playedGames: Int,
    @Json(name = "won") val won: Int,
    @Json(name = "draw") val draw: Int,
    @Json(name = "lost") val lost: Int,
    @Json(name = "points") val points: Int,
    @Json(name = "goalsFor") val goalsFor: Int,
    @Json(name = "goalsAgainst") val goalsAgainst: Int,
    @Json(name = "goalDifference") val goalDifference: Int
)

@JsonClass(generateAdapter = true)
data class StandingTeam(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "tla") val tla: String
)
