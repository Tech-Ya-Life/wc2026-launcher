package com.wc2026.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.schedule.MatchDatabase
import com.wc2026.launcher.schedule.MatchScheduleRepo
import com.wc2026.launcher.schedule.Standing
import com.wc2026.launcher.schedule.StandingsRepo
import com.wc2026.launcher.settings.AppSettings
import com.wc2026.launcher.settings.SettingsRepository
import com.wc2026.launcher.theme.LauncherTheme
import com.wc2026.launcher.theme.ThemeEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val db            = MatchDatabase.getInstance(app)
    private val repo          = MatchScheduleRepo.getInstance(dao = db.matchDao())
    private val standingsRepo = StandingsRepo()
    private val settings      = SettingsRepository.getInstance(app)

    // ── Match data ────────────────────────────────────────────────────────────

    /** Next upcoming or currently live match */
    val nextMatch: StateFlow<Match?> = repo.nextMatch()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Most recent finished match — shown when nothing is upcoming/live */
    val lastMatch: StateFlow<Match?> = repo.lastFinishedMatch()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val allMatches: StateFlow<List<Match>> = repo.allMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Settings ──────────────────────────────────────────────────────────────

    val appSettings: StateFlow<AppSettings> = settings.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    // ── Theme — respects pin > auto-theme > default ───────────────────────────

    val theme: StateFlow<LauncherTheme> = combine(nextMatch, lastMatch, appSettings) { next, last, s ->
        val match = next ?: last  // use last finished match for auto-theme when nothing live
        when {
            s.favouriteTeam.isNotBlank() -> ThemeEngine.fromTeam(s.favouriteTeam)
            s.autoTheme && match != null -> ThemeEngine.fromMatch(match)
            else                         -> ThemeEngine.default
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeEngine.default)

    // ── Group standings — fetched lazily when the setting is enabled ──────────

    private val _standings = MutableStateFlow<List<Standing>>(emptyList())
    val standings: StateFlow<List<Standing>> = _standings.asStateFlow()

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        // Initial sync on launch
        viewModelScope.launch { repo.sync() }

        // Adaptive refresh: 30s during live matches, 5 min otherwise
        viewModelScope.launch {
            while (true) {
                val isLive = nextMatch.value?.isLive == true
                delay(if (isLive) 30_000L else 5 * 60_000L)
                repo.sync()
            }
        }

        // Fetch standings the first time the user enables the widget
        viewModelScope.launch {
            appSettings.collect { s ->
                if (s.showStandings && _standings.value.isEmpty()) {
                    standingsRepo.getGroupStandings()
                        .onSuccess { _standings.value = it }
                }
            }
        }
    }
}
