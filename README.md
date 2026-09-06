# Aperonix - Voice-first AI Assistant (Phase 1)

This repository contains the initial Android project skeleton for the Aperonix voice-first assistant.

Phase 1 delivered items:
- Android project structure
- Gradle Kotlin DSL files: settings.gradle.kts, build.gradle.kts, app/build.gradle.kts
- Basic MainActivity in Jetpack Compose showing center logo and status text
- AndroidManifest with required permissions (INTERNET, RECORD_AUDIO)
- Resource placeholders (logo vector placeholder, colors, styles)

Gemini API key configuration:
- Use local.properties in your development environment with the following entry:

GEMINI_API_KEY=YOUR_GEMINI_API_KEY

Do NOT commit your real API key to the repository.

How to build (Android Studio recommended):
1. Open the project in Android Studio.
2. Add the supplied logo image to app/src/main/res/drawable/ic_aperonix_logo.png (replace the placeholder vector).
3. Put your Gemini API key in local.properties (do NOT commit it).
4. Sync Gradle and run the app.

Notes:
- This is Phase 1 only. Core app skeleton and build config created. Next phases will implement AI wiring, voice managers, Room, DataStore, actions, settings and full UI states.
