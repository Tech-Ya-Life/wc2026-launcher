package com.wc2026.launcher.schedule

import android.util.Log
import com.wc2026.launcher.BuildConfig

class StandingsRepo(
    private val api: FootballDataApi = FootballDataApi.create()
) {
    private val tag = "StandingsRepo"

    /**
     * Fetch group-stage standings sorted alphabetically by group letter.
     * Returns an empty list on error (no API key, network failure, etc.)
     */
    suspend fun getGroupStandings(): Result<List<Standing>> = runCatching {
        api.getWorldCupStandings(apiKey = BuildConfig.FOOTBALL_DATA_API_KEY)
            .standings
            // Accept any standing that has a group label (GROUP_A … GROUP_L etc.)
            .filter { it.group != null }
            // Prefer TOTAL type if multiple types exist for the same group
            .groupBy { it.group }
            .mapValues { (_, entries) ->
                entries.firstOrNull { it.type == "TOTAL" } ?: entries.first()
            }
            .values
            .sortedBy { it.group }
    }.onFailure {
        Log.e(tag, "Standings fetch failed: ${it.message}")
    }
}
