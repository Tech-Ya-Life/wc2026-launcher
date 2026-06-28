package com.wc2026.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.schedule.MatchDatabase
import com.wc2026.launcher.schedule.MatchScheduleRepo
import com.wc2026.launcher.schedule.FootballDataApi
import com.wc2026.launcher.theme.LauncherTheme
import com.wc2026.launcher.theme.ThemeEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val db   = MatchDatabase.getInstance(app)
    private val repo = MatchScheduleRepo.getInstance(dao = db.matchDao())

    /** The next or live match — drives the theme */
    val nextMatch: StateFlow<Match?> = repo.nextMatch()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Derived theme — updates automatically when nextMatch changes */
    val theme: StateFlow<LauncherTheme> = nextMatch
        .map { match -> match?.let { ThemeEngine.fromMatch(it) } ?: ThemeEngine.default }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeEngine.default)

    /** All matches, for the schedule screen */
    val allMatches: StateFlow<List<Match>> = repo.allMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Kick off an immediate sync on first launch
        viewModelScope.launch { repo.sync() }
    }
}
