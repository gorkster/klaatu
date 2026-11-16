# Klaatu

A modern, widget-first Android RSS/Atom feed reader built in **Kotlin**, inspired by the classic Palabre scrollable home screen widget.

## Features
- **Scrollable Home Screen Widget** (Jetpack Glance):
  - Rounded card container with a themed header bar (feed/folder title, item count, refresh action, settings action).
  - Compact yet readable article rows: rounded thumbnail on the left, 2-line title, and source name + relative timestamp on the right.
  - Tapping any article opens it directly in your default browser.
  - Multiple independent widgets: each home screen widget can have its own OPML folder(s), individual feeds, custom header label, and color theme (`WidgetConfigActivity`).
  - 12 Material-inspired two-tone themes (darker header + matching light/dark tonal body): **Ocean Blue**, **Teal**, **Indigo**, **Sage**, **Terracotta**, **Plum**, **Amber**, **Rose**, **Slate**, **AMOLED Dark**, **Dark Indigo**, and **Dark Forest**.
- **OPML Support**: Import and export feed subscriptions and folders/categories via standard OPML 2.0 XML (`OpmlCodec`).
- **RSS 2.0 & Atom Sync**: Background and manual feed fetching (`FeedParser`, `FeedSyncWorker`) with local Room caching and Coil thumbnail loading.
- **Companion App** (Jetpack Compose + Material 3): Manage feeds/categories, import/export OPML, and view/configure all placed home screen widgets.

## Tech Stack
- **Language**: Kotlin
- **Widget**: Jetpack Glance (`glance-appwidget`, `glance-material3`)
- **App UI**: Jetpack Compose + Material 3
- **Persistence**: Room (SQLite + KSP)
- **Background Sync**: WorkManager
- **Networking & Parsing**: OkHttp + `javax.xml.parsers` DOM (`OpmlCodec`, `FeedParser`)
- **Image Loading**: Coil

## Development Environment Setup
This repository is configured for local builds on Linux (Fedora) using user-space tools:
- **JDK**: Eclipse Temurin JDK 21 (`~/.local/jdks/jdk-21`)
- **Android SDK**: `~/Android/Sdk` (`cmdline-tools`, `platform-tools`, `emulator`, `platforms;android-35`, `build-tools;35.0.0`, `system-images;android-35;google_apis;x86_64`)
- **AVD**: `klaatu_pixel` (Pixel 7, Android 15 / API 35)
- **Environment variables** (added to `~/.zshrc`):
  ```bash
  export JAVA_HOME="$HOME/.local/jdks/jdk-21"
  export ANDROID_HOME="$HOME/Android/Sdk"
  export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$PATH"
  ```

### Running on the Emulator
```bash
emulator -avd klaatu_pixel &
./gradlew installDebug
```

### Building from the Command Line
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

### Decision Log
See [DECISIONS.md](DECISIONS.md) for architecture and tooling decisions.
