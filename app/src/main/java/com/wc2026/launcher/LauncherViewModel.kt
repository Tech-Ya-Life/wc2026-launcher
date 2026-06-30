package com.wc2026.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wc2026.launcher.schedule.Match
import com.wc2026.launcher.schedule.MatchDatabase
import com.wc2026.launcher.schedule.MatchScheduleRepo
import com.wc2026.launcher.schedule.PlayerImageRepo
import com.wc2026.launcher.schedule.Standing
import com.wc2026.launcher.schedule.StandingsRepo
import com.wc2026.launcher.theme.StarPlayerFullNames
import com.wc2026.launcher.settings.AppSettings
import com.wc2026.launcher.settings.SettingsRepository
import com.wc2026.launcher.theme.LauncherTheme
import com.wc2026.launcher.theme.ThemeEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val db             = MatchDatabase.getInstance(app)
    private val repo           = MatchScheduleRepo.getInstance(dao = db.matchDao())
    private val standingsRepo  = StandingsRepo()
    private val settings       = SettingsRepository.getInstance(app)
    private val playerImageRepo = PlayerImageRepo()

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

    // ── Player images — TLA → photo URL (fetched from TheSportsDB) ────────────

    private val _playerImages = MutableStateFlow<Map<String, String>>(emptyMap())
    val playerImages: StateFlow<Map<String, String>> = _playerImages.asStateFlow()

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

        // Fetch player images whenever the displayed match changes teams
        viewModelScope.launch {
            combine(nextMatch, lastMatch) { next, last -> next ?: last }
                .collect { match ->
                    match ?: return@collect
                    fetchPlayerImage(match.homeTeamTla)
                    fetchPlayerImage(match.awayTeamTla)
                }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private suspend fun fetchPlayerImage(tla: String) {
        if (_playerImages.value.containsKey(tla)) return      // already cached
        val searchName = StarPlayerFullNames.forTla(tla) ?: return
        val url = playerImageRepo.fetchUrl(searchName) ?: return
        _playerImages.update { it + (tla to url) }
    }
}
