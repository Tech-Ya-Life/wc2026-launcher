# ⚽ WC2026 Launcher

A custom Android home screen launcher themed around the **FIFA World Cup 2026**.

The launcher automatically changes its look based on who's playing next — colors, wallpaper tints, and widgets all shift to reflect the two competing teams in real time.

---

## ✨ Features

- **Match-Driven Themes** — Home screen colors automatically match the next upcoming fixture's two teams
- **Live Schedule Widget** — Countdown to next match, live scores during games, group standings
- **Team Color Palettes** — Every national team has a defined color scheme (home + away kit)
- **Smooth Transitions** — Animated wallpaper + color shift when the next match is loaded
- **Favorite Team Pin** — Override auto-theme and lock to your team's colors
- **Customization Settings** — Theme intensity, clock style, notification alerts, language

---

## 🗺️ Project Structure

```
wc2026-launcher/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wc2026/launcher/
│   │   │   │   ├── LauncherActivity.kt        # Home screen entry point
│   │   │   │   ├── AppGridAdapter.kt          # App icon grid
│   │   │   │   ├── AppDrawer.kt               # Swipe-up app drawer
│   │   │   │   ├── schedule/
│   │   │   │   │   ├── MatchScheduleRepo.kt   # Fetches & caches schedule
│   │   │   │   │   ├── MatchSyncWorker.kt     # Background sync (every 5 min)
│   │   │   │   │   └── MatchModel.kt          # Data models
│   │   │   │   ├── theme/
│   │   │   │   │   ├── ThemeEngine.kt         # Core color-switching logic
│   │   │   │   │   ├── TeamColorPalette.kt    # All 48 team color definitions
│   │   │   │   │   └── WallpaperManager.kt    # Wallpaper tint controller
│   │   │   │   ├── widgets/
│   │   │   │   │   ├── MatchCountdownWidget.kt
│   │   │   │   │   ├── LiveScoreWidget.kt
│   │   │   │   │   └── StandingsWidget.kt
│   │   │   │   └── settings/
│   │   │   │       └── SettingsActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/                    # XML layouts
│   │   │   │   ├── drawable/                  # Icons & graphics
│   │   │   │   └── values/                    # Colors, strings, themes
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| API Client | Retrofit + OkHttp |
| Local Cache | Room Database |
| Background Sync | WorkManager |
| Settings Storage | DataStore |
| Image Loading | Coil |
| Schedule Data | football-data.org API |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later (free download at developer.android.com)
- Android device or emulator running API 26+ (Android 8.0+)
- Free API key from [football-data.org](https://www.football-data.org/client/register)

### Setup
1. Clone this repo: `git clone https://github.com/YOUR_USERNAME/wc2026-launcher.git`
2. Open in Android Studio
3. Add your API key to `local.properties`:
   ```
   FOOTBALL_DATA_API_KEY=your_key_here
   ```
4. Run on device or emulator

---

## 🎨 How the Theme Engine Works

```
Every 5 minutes (or on launch):
  ↓
Fetch next scheduled WC 2026 match
  ↓
Look up Team A color palette + Team B color palette
  ↓
Blend into a dual-tone theme (Team A left / Team B right)
  ↓
Apply to: wallpaper tint · widget backgrounds · icon outlines · clock color
  ↓
Animate transition (500ms crossfade)
```

---

## 📅 Build Roadmap

- [ ] Phase 1 — Launcher shell (home screen + app grid)
- [ ] Phase 2 — Schedule API integration + background sync
- [ ] Phase 3 — Theme engine + team color palettes
- [ ] Phase 4 — Widgets (countdown, live score, standings)
- [ ] Phase 5 — Settings screen + user customization
- [ ] Phase 6 — Polish, animations, edge cases

---

## 📝 License

MIT License — feel free to fork and build on it.