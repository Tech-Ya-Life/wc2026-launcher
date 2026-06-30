package com.wc2026.launcher.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/** Single DataStore instance per process */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_settings")

object AppSettingsKeys {
    val AUTO_THEME      = booleanPreferencesKey("auto_theme")
    val FAV_TEAM        = stringPreferencesKey("favourite_team")  // TLA or "" (no pin)
    val MATCH_ALERTS    = booleanPreferencesKey("match_alerts")
    val SHOW_SCORES     = booleanPreferencesKey("show_live_scores")
    val SHOW_STANDINGS  = booleanPreferencesKey("show_standings")
}

data class AppSettings(
    val autoTheme: Boolean      = true,
    val favouriteTeam: String   = "",     // "" = follow next match; else TLA pin
    val matchAlerts: Boolean    = false,
    val showLiveScores: Boolean = true,
    val showStandings: Boolean  = false
)
