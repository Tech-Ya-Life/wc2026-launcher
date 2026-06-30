package com.wc2026.launcher.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

// ─────────────────────────────────────────────
//  API response models (from football-data.org)
// ─────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class MatchesResponse(
    @Json(name = "matches") val matches: List<ApiMatch>
)

@JsonClass(generateAdapter = true)
data class ApiMatch(
    @Json(name = "id") val id: Int,
    @Json(name = "utcDate") val utcDate: String,       // ISO-8601
    @Json(name = "status") val status: String,          // SCHEDULED, LIVE, IN_PLAY, FINISHED
    @Json(name = "stage") val stage: String,
    @Json(name = "homeTeam") val homeTeam: ApiTeam,
    @Json(name = "awayTeam") val awayTeam: ApiTeam,
    @Json(name = "score") val score: ApiScore
)

@JsonClass(generateAdapter = true)
data class ApiTeam(
    @Json(name = "id") val id: Int?,           // null for knockout TBD slots
    @Json(name = "name") val name: String?,    // null for knockout TBD slots
    @Json(name = "shortName") val shortName: String?,
    @Json(name = "tla") val tla: String?,      // null for knockout TBD slots
    @Json(name = "crest") val crest: String?
)

@JsonClass(generateAdapter = true)
data class ApiScore(
    @Json(name = "fullTime") val fullTime: ApiGoals
)

@JsonClass(generateAdapter = true)
data class ApiGoals(
    @Json(name = "home") val home: Int?,
    @Json(name = "away") val away: Int?
)

// ─────────────────────────────────────────────
//  Local DB entity (cached in Room)
// ─────────────────────────────────────────────

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey val id: Int,
    val utcDate: String,
    val status: String,
    val stage: String,
    val homeTeamName: String,
    val homeTeamTla: String,
    val homeTeamCrest: String?,
    val awayTeamName: String,
    val awayTeamTla: String,
    val awayTeamCrest: String?,
    val homeScore: Int?,
    val awayScore: Int?
) {
    val isLive get() = status == "IN_PLAY" || status == "LIVE"
    val isFinished get() = status == "FINISHED"
    val isUpcoming get() = status == "SCHEDULED" || status == "TIMED"
}

// ─────────────────────────────────────────────
//  Conversion helper
// ─────────────────────────────────────────────

fun ApiMatch.toMatch() = Match(
    id = id,
    utcDate = utcDate,
    status = status,
    stage = stage,
    homeTeamName  = homeTeam.name  ?: "TBD",
    homeTeamTla   = homeTeam.tla   ?: "TBD",
    homeTeamCrest = homeTeam.crest,
    awayTeamName  = awayTeam.name  ?: "TBD",
    awayTeamTla   = awayTeam.tla   ?: "TBD",
    awayTeamCrest = awayTeam.crest,
    homeScore = score.fullTime.home,
    awayScore = score.fullTime.away
)
