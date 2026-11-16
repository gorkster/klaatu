# Architecture & Tooling Decision Log

## ADR-001: Kotlin as Primary Language
- **Date**: 2026-11-15
- **Status**: Accepted
- **Decision**: Write the entire Android application, widget, and Gradle build scripts (`*.gradle.kts`) in Kotlin.
- **Rationale**: First-class Android support, Jetpack Compose & Glance compatibility, coroutines for async feed fetching, and concise null-safe data modeling.

## ADR-002: Jetpack Glance for the Scrollable Widget
- **Date**: 2026-11-15
- **Status**: Accepted
- **Decision**: Use `androidx.glance:glance-appwidget` (with `glance-material3`) instead of legacy XML `RemoteViewsService` + `RemoteViewsFactory`.
- **Rationale**: Glance provides a declarative Compose-style `LazyColumn` for scrollable widgets, cleaner per-widget state & theming (including Material You dynamic colors and custom color palettes), and significantly less boilerplate.

## ADR-003: Open Article Links in Default Browser
- **Date**: 2026-11-15
- **Status**: Accepted
- **Decision**: Tapping an article in the widget launches `Intent.ACTION_VIEW` with the article URL rather than an in-app WebView or Custom Tab.
- **Rationale**: Matches user preference for reading in their configured default browser with existing extensions, ad blockers, and history.

## ADR-004: Local-First Storage & Background Sync Stack
- **Date**: 2026-11-15
- **Status**: Accepted
- **Decision**: Use Room for local caching of feeds, categories, and articles; WorkManager for periodic background sync; OkHttp + `XmlPullParser` for RSS/Atom/OPML parsing; and Coil for thumbnail/favicon caching.
- **Rationale**: Lightweight, zero-backend architecture with reliable offline widget rendering and standard Android background scheduling.

## ADR-005: User-Space JDK 21 & Android SDK Bootstrap
- **Date**: 2026-11-15
- **Status**: Accepted
- **Decision**: Install Eclipse Temurin JDK 21 (`~/.local/jdks/jdk-21`) and Android SDK (`~/Android/Sdk`) in user-space rather than system-wide via `dnf`.
- **Rationale**: Avoids needing root/sudo for toolchain setup, isolates Android builds from the system JRE 25, and provides a reproducible `JAVA_HOME` / `ANDROID_HOME` setup compatible with Android Gradle Plugin.
