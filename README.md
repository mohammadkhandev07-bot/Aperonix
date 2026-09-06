# README - Final notes

The project is implemented to a full, buildable Android application skeleton for Aperonix. The official logo image file was intentionally left out of the repository by request. To display the official logo and to generate launcher icons, please add your provided PNG to:

  app/src/main/res/drawable/ic_aperonix_logo.png

and then rebuild the project.

Important build steps
1. Add GEMINI API key to your local.properties (do NOT commit it):

   GEMINI_API_KEY=YOUR_GEMINI_API_KEY

2. Optionally add the official Aperonix logo as mentioned above.
3. Build:

   ./gradlew clean assembleDebug

APK output (after successful build):

   app/build/outputs/apk/debug/app-debug.apk

Gemini / AI note
- GeminiClient currently contains a placeholder endpoint and a pluggable request/response parsing layer. Replace the placeholder URL and adapt the request payload/response parsing to the official Gemini REST API format you plan to use.

Security note
- Never commit your API key or other secrets to Git. A distributed APK cannot fully hide an embedded key.

If you run into build errors, paste the Gradle output here and I will fix issues and push patches.
