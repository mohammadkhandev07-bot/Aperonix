# README - Update

## New features implemented
- Settings UI scaffolding (voice settings, speech rate, pitch, memory toggle, Gemini connection test)
- Conversation history and Memory screens (Compose)
- Confirmation dialog component
- Splash screen composable
- GeminiClient improved parsing for structured action output and friendly error cases
- MemoryRepository getAll and MemoryScreen UI
- Additional unit test for Gemini client action extraction

## Next steps / Notes
- Add the supplied exact logo image file at `app/src/main/res/drawable/ic_aperonix_logo.png`. The project currently references `ic_aperonix_logo` drawable; replace the placeholder vector with the supplied PNG to see the final logo.
- Replace Gemini placeholder endpoint in `GeminiClient` with the real Gemini REST endpoint and adjust request/response keys as necessary.
- Build & run: Open the project in Android Studio, set `GEMINI_API_KEY` in your `local.properties` (do NOT commit), then run `./gradlew clean assembleDebug`.
- APK location: `app/build/outputs/apk/debug/app-debug.apk` after successful build.
