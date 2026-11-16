# Klaatu

A modern, widget-first Android RSS/Atom feed reader built in **Kotlin**, inspired by the classic Palabre scrollable home screen widget.

## Features (Planned & In Progress)
- **Scrollable Home Screen Widget** (Jetpack Glance):
  - Rounded card container with a themed header bar (feed/folder title, item count, refresh action, settings action).
  - Compact yet readable article rows: rounded thumbnail on the left, 2-line title, and feed favicon + source name + relative timestamp on the right.
  - Tapping any article opens it directly in your default browser.
  - Multiple color themes (Material You Dynamic Color, Classic Blue, AMOLED Dark, Light, Slate, etc.).
- **OPML Support**: Import and export feed subscriptions and folders/categories via standard OPML XML.
- **Minimal Companion App** (Jetpack Compose + Material 3): Manage feeds/categories, import/export OPML, and configure per-widget themes and refresh cadence.

## Tech Stack
- **Language**: Kotlin
- **Widget**: Jetpack Glance (`glance-appwidget`, `glance-material3`)
- **App UI**: Jetpack Compose + Material 3
- **Persistence**: Room (SQLite)
- **Background Sync**: WorkManager
- **Networking & Parsing**: OkHttp + Android `XmlPullParser` (RSS 2.0, Atom, OPML)
- **Image Loading**: Coil

## Development Environment Setup
This repository is configured for local builds on Linux (Fedora) using user-space tools:
- **JDK**: Eclipse Temurin JDK 21 (`~/.local/jdks/jdk-21`)
- **Android SDK**: `~/Android/Sdk` (`cmdline-tools`, `platform-tools`, `platforms;android-35`, `build-tools;35.0.0`)
- **Environment variables** (added to `~/.zshrc`):
  ```bash
  export JAVA_HOME="$HOME/.local/jdks/jdk-21"
  export ANDROID_HOME="$HOME/Android/Sdk"
  export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
  ```

### Building from the Command Line
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

### Decision Log
See [DECISIONS.md](DECISIONS.md) for architecture and tooling decisions.
