package com.wc2026.launcher.schedule

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY utcDate ASC")
    fun getAllMatches(): Flow<List<Match>>

    /** The next upcoming or currently live match */
    @Query("""
        SELECT * FROM matches
        WHERE status IN ('SCHEDULED', 'TIMED', 'IN_PLAY', 'LIVE')
        ORDER BY utcDate ASC
        LIMIT 1
    """)
    fun getNextMatch(): Flow<Match?>

    /** Today's matches */
    @Query("""
        SELECT * FROM matches
        WHERE utcDate LIKE :datePrefix || '%'
        ORDER BY utcDate ASC
    """)
    fun getMatchesOnDate(datePrefix: String): Flow<List<Match>>

    /** The most recently finished match — shown when no upcoming/live match exists */
    @Query("""
        SELECT * FROM matches
        WHERE status = 'FINISHED'
        ORDER BY utcDate DESC
        LIMIT 1
    """)
    fun getLastFinishedMatch(): Flow<Match?>

    /** One-shot query for upcoming matches — used by [MatchSyncWorker] to schedule alerts */
    @Query("""
        SELECT * FROM matches
        WHERE status IN ('SCHEDULED', 'TIMED')
        AND utcDate > :nowIso
        ORDER BY utcDate ASC
    """)
    suspend fun getUpcomingMatches(nowIso: String): List<Match>

    @Upsert
    suspend fun upsertMatches(matches: List<Match>)

    @Query("DELETE FROM matches")
    suspend fun clearAll()
}
