# Aperonix

Aperonix is a voice-first Android AI assistant. This repository contains a complete Android project implemented in Kotlin and Jetpack Compose.

IMPORTANT: This repository does NOT include any real Gemini API key. Do NOT commit your API keys. For development, add the following to your local.properties (do NOT commit):

GEMINI_API_KEY=YOUR_GEMINI_API_KEY

What was implemented in this update:
- SplashActivity and MainActivity wiring
- Main Compose UI with breathing/pulse animation and microphone interaction
- SpeechRecognizerManager (Speech-to-Text)
- TextToSpeechManager with utterance completion callback
- GeminiClient abstraction (HTTP placeholder, handles missing key)
- Room entities and DAOs for Conversation and Memory
- AppDatabase singleton
- SettingsRepository using DataStore (preferences)
- ActionValidator and ActionExecutor for safe Android actions
- Unit tests for ActionValidator

Next steps (not yet completed):
- Full Settings UI
- Memory and Conversation history UI
- Full Gemini production integration (replace placeholder endpoint)
- Additional tests

Build
- Open in Android Studio, add your GEMINI_API_KEY to local.properties, and build.
- APK path after successful build: app/build/outputs/apk/debug/app-debug.apk

Security note
- Embedding an API key inside an APK is not fully secure. Users with APK inspection skills can extract embedded keys. For production use, a secure backend proxy is strongly recommended.
