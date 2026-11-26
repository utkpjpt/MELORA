<div align="center">
  <img src="app/src/main/res/drawable-nodpi/melora_logo.png" width="128" alt="MELORA logo">
  <h1>MELORA</h1>
  <p><strong>Version 1 — An open-source Android music player with a Liquid Glass interface, synced lyrics, and listening insights.</strong></p>
  <p>
    <a href="https://github.com/utkpjpt/MELORA/releases/latest/download/MELORA-v1-arm64.apk">
      <img src="https://img.shields.io/badge/Download_APK-Version_1-E53935?style=for-the-badge&logo=android&logoColor=white" alt="Download MELORA APK">
    </a>
    <img src="https://img.shields.io/badge/Version-1-5268D8?style=for-the-badge&logo=android&logoColor=white" alt="MELORA Version 1">
    <img src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 8.0 and later">
    <img src="https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge" alt="GPL-3.0 license">
  </p>
</div>

**MELORA** (`com.melora.music`) brings music discovery, streaming playback, synchronized word-level lyrics, and listening analytics together in one Android app built with Kotlin, Jetpack Compose, and Media3.

## Download

Grab the latest Android APK directly from GitHub Releases:

- **Direct APK (`arm64-v8a`, Version 1):** [MELORA-v1-arm64.apk](https://github.com/utkpjpt/MELORA/releases/latest/download/MELORA-v1-arm64.apk)
- **All Releases:** [github.com/utkpjpt/MELORA/releases](https://github.com/utkpjpt/MELORA/releases)

> Requires **Android 8.0+** (`arm64-v8a` device). Allow installation from unknown sources when sideloading the APK.

## Screenshots

<div align="center">
<table>
  <tr>
    <td align="center"><img src="assets/screenshots/home.jpg" width="200" alt="MELORA Home"><br><sub><b>Home</b></sub></td>
    <td align="center"><img src="assets/screenshots/player.jpg" width="200" alt="MELORA Now Playing"><br><sub><b>Now Playing</b></sub></td>
    <td align="center"><img src="assets/screenshots/library.jpg" width="200" alt="MELORA Library"><br><sub><b>Library</b></sub></td>
    <td align="center"><img src="assets/screenshots/search.jpg" width="200" alt="MELORA Search"><br><sub><b>Search</b></sub></td>
  </tr>
  <tr>
    <td align="center"><img src="assets/screenshots/stats.jpg" width="200" alt="MELORA Stats"><br><sub><b>Stats</b></sub></td>
    <td align="center"><img src="assets/screenshots/appearance.jpg" width="200" alt="MELORA Appearance"><br><sub><b>Appearance</b></sub></td>
    <td align="center"><img src="assets/screenshots/player-lyrics.jpg" width="200" alt="MELORA Player & Lyrics Settings"><br><sub><b>Player & Lyrics</b></sub></td>
    <td align="center"><img src="assets/screenshots/about.jpg" width="200" alt="About MELORA"><br><sub><b>About</b></sub></td>
  </tr>
</table>
</div>

## Features

### Playback & Audio
- Stream music via InnerTube with queue management, sleep timer, playback speed, and equalizer support.
- Background playback with media notification, lock-screen, and Android Auto controls.
- Crossfade, AutoMix, and DJ-style song transitions.

### Lyrics & Visuals
- Multi-provider synchronized lyrics with word/syllable timing, translation, and romanization.
- Fixed active-line **Liquid Glass** capsule highlight and calibrated word glow.
- Customizable player themes, adaptive artwork colors, Liquid Glass surfaces, and **Make your own** DIY theme designer.

### Integrations & Analytics
- **Discord Rich Presence** activity status and **Last.fm** scrobbling.
- Redesigned **Stats** page with listening time overview, play counts, `#1 On Repeat` spotlight, and ranked track breakdowns.
- **Listen Together** party sessions and Spotify / Apple Music playlist import.

## Credits
- **Lead Developer:** ZIOLEX VT
- **Side Developer:** UTKARSH PRAJAPATI

## Project Structure

MELORA is a multi-module Gradle Android project:

| Path | Responsibility |
| --- | --- |
| `app/` | Android application (`com.melora.music`), screens, ViewModels, navigation, playback service, and persistence. |
| `hig/` | Shared Cupertino-style controls, theme primitives, accessibility helpers, and Liquid Glass utilities. |
| `innertube/` | InnerTube client and catalog models. |
| `lyrics/` | Lyrics providers, TTML/LRC parsing, romanization, and translation. |
| `canvas/` | Animated canvas artwork support. |
| `artwork/` | Artwork loading, caching, and blur processing. |
| `spotify/` | Spotify authentication and playlist import. |
| `integrations/` | Discord Rich Presence, Last.fm, and music recognition. |

## Build Locally

### Requirements
- JDK 21
- Android Studio with Android SDK 37 installed
- Android NDK (`27.0.12077973`) for native C++ analysis libraries

To build the compressed ARM64 release APK:

```bash
./gradlew :app:assembleArm64FossRelease
```

On Windows PowerShell:

```powershell
.\gradlew :app:assembleArm64FossRelease --console=plain
```

## Legal & License

MELORA is an independent project built upon open-source Convx foundations and is not affiliated with YouTube, Google, or their affiliates.

Licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE) and [NOTICE](NOTICE) for full license and attribution details.
