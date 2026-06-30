package com.wc2026.launcher.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.wc2026.launcher.theme.TeamColorPalette
import com.wc2026.launcher.ui.TeamPickerSheet

class SettingsActivity : ComponentActivity() {

    private val vm: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val settings by vm.settings.collectAsState()
            SettingsScreen(
                settings         = settings,
                onAutoTheme      = vm::setAutoTheme,
                onPinTeam        = vm::pinFavTeam,
                onClearTeam      = vm::clearFavTeam,
                onMatchAlerts    = vm::setMatchAlerts,
                onShowLiveScores = vm::setShowLiveScores,
                onShowStandings  = vm::setShowStandings,
                onBack           = ::finish
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onAutoTheme: (Boolean) -> Unit,
    onPinTeam: (String) -> Unit,
    onClearTeam: () -> Unit,
    onMatchAlerts: (Boolean) -> Unit,
    onShowLiveScores: (Boolean) -> Unit,
    onShowStandings: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var showTeamPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1B5E20))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ── Top bar ─────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("⚙️  Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Theme ────────────────────────────────────────────
            SettingSection(title = "Theme") {
                SettingRow(
                    label = "Match-driven auto theme",
                    description = "Changes colours based on the next fixture",
                    checked = settings.autoTheme,
                    onCheckedChange = onAutoTheme
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)

                // Favourite team pin — toggling ON opens the picker
                val favName = settings.favouriteTeam
                    .takeIf { it.isNotBlank() }
                    ?.let { TeamColorPalette.palettes[it]?.name }
                SettingRow(
                    label = "Favourite team pin",
                    description = if (favName != null) "Locked to $favName" else "Lock theme to your team",
                    checked = settings.favouriteTeam.isNotBlank(),
                    onCheckedChange = { on ->
                        if (on) showTeamPicker = true else onClearTeam()
                    }
                )
            }

            // ── Notifications ─────────────────────────────────────
            SettingSection(title = "Notifications") {
                SettingRow(
                    label = "Match start alerts",
                    description = "Get notified 15 mins before kick-off",
                    checked = settings.matchAlerts,
                    onCheckedChange = onMatchAlerts
                )
            }

            // ── Display ───────────────────────────────────────────
            SettingSection(title = "Display") {
                SettingRow(
                    label = "Show live scores",
                    description = "Display score during and after matches",
                    checked = settings.showLiveScores,
                    onCheckedChange = onShowLiveScores
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 0.5.dp)
                SettingRow(
                    label = "Show group standings",
                    description = "Standings widget — coming in next update",
                    checked = settings.showStandings,
                    onCheckedChange = onShowStandings
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Footer ─────────────────────────────────────────────
            Text(
                text = "⚽  FIFA World Cup 2026  •  USA · Canada · Mexico",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }

    // Team picker bottom sheet
    if (showTeamPicker) {
        TeamPickerSheet(
            currentTla = settings.favouriteTeam,
            onTeamSelected = { tla ->
                onPinTeam(tla)
                showTeamPicker = false
            },
            onDismiss = { showTeamPicker = false }
        )
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            title.uppercase(),
            color = Color(0xFFFFD700),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(description, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFFD700)
            )
        )
    }
}
