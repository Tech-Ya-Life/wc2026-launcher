package com.wc2026.launcher.schedule

import android.util.Log
import com.wc2026.launcher.BuildConfig
import kotlinx.coroutines.flow.Flow

class MatchScheduleRepo(
    private val api: FootballDataApi,
    private val dao: MatchDao
) {
    private val tag = "MatchScheduleRepo"

    /** Observe the next match (live or upcoming) from the local cache */
    fun nextMatch(): Flow<Match?> = dao.getNextMatch()

    /** Observe all matches from the local cache */
    fun allMatches(): Flow<List<Match>> = dao.getAllMatches()

    /**
     * Fetch fresh data from football-data.org and update the local cache.
     * Called by MatchSyncWorker on a schedule, and on app launch.
     */
    suspend fun sync(): Result<Unit> = runCatching {
        val response = api.getWorldCupMatches(
            apiKey = BuildConfig.FOOTBALL_DATA_API_KEY
        )
        val matches = response.matches.map { it.toMatch() }
        dao.upsertMatches(matches)
        Log.d(tag, "Synced ${matches.size} matches")
    }.onFailure {
        Log.e(tag, "Sync failed: ${it.message}")
    }

    companion object {
        @Volatile private var INSTANCE: MatchScheduleRepo? = null

        fun getInstance(
            api: FootballDataApi = FootballDataApi.create(),
            dao: MatchDao
        ): MatchScheduleRepo =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MatchScheduleRepo(api, dao).also { INSTANCE = it }
            }
    }
}
