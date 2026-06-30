package com.wc2026.launcher.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository private constructor(context: Context) {

    private val store = context.dataStore

    val settings: Flow<AppSettings> = store.data.map { prefs ->
        AppSettings(
            autoTheme      = prefs[AppSettingsKeys.AUTO_THEME]     ?: true,
            favouriteTeam  = prefs[AppSettingsKeys.FAV_TEAM]       ?: "",
            matchAlerts    = prefs[AppSettingsKeys.MATCH_ALERTS]   ?: false,
            showLiveScores = prefs[AppSettingsKeys.SHOW_SCORES]    ?: true,
            showStandings  = prefs[AppSettingsKeys.SHOW_STANDINGS] ?: false
        )
    }

    suspend fun setAutoTheme(v: Boolean)      = store.edit { it[AppSettingsKeys.AUTO_THEME]     = v }
    suspend fun setFavTeam(tla: String)       = store.edit { it[AppSettingsKeys.FAV_TEAM]       = tla }
    suspend fun setMatchAlerts(v: Boolean)    = store.edit { it[AppSettingsKeys.MATCH_ALERTS]   = v }
    suspend fun setShowLiveScores(v: Boolean) = store.edit { it[AppSettingsKeys.SHOW_SCORES]    = v }
    suspend fun setShowStandings(v: Boolean)  = store.edit { it[AppSettingsKeys.SHOW_STANDINGS] = v }

    companion object {
        @Volatile private var INSTANCE: SettingsRepository? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            SettingsRepository(context.applicationContext).also { INSTANCE = it }
        }
    }
}
