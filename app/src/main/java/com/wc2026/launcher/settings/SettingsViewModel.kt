package com.wc2026.launcher.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SettingsRepository.getInstance(app)

    val settings = repo.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AppSettings()
    )

    fun setAutoTheme(v: Boolean)      = viewModelScope.launch { repo.setAutoTheme(v) }
    fun setMatchAlerts(v: Boolean)    = viewModelScope.launch { repo.setMatchAlerts(v) }
    fun setShowLiveScores(v: Boolean) = viewModelScope.launch { repo.setShowLiveScores(v) }
    fun setShowStandings(v: Boolean)  = viewModelScope.launch { repo.setShowStandings(v) }
    /** Phase 6: team picker will call this */
    fun pinFavTeam(tla: String)       = viewModelScope.launch { repo.setFavTeam(tla) }
    fun clearFavTeam()                = viewModelScope.launch { repo.setFavTeam("") }
}
