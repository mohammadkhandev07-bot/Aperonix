package com.aperonix.ai.ai

import android.util.Log
import com.aperonix.ai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

sealed class GeminiResult {
    data class Success(val text: String) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}

class GeminiClient(private val httpClient: OkHttpClient = OkHttpClient()) {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    suspend fun generateText(prompt: String): GeminiResult {
        if (apiKey.isBlank()) {
            return GeminiResult.Error("Gemini API key missing. Configure GEMINI_API_KEY in local.properties.")
        }

        // Placeholder minimal implementation that calls a hypothetical Gemini REST endpoint.
        // NOTE: Real Gemini API usage and endpoints vary; for now we provide a simple HTTP-based wrapper
        // that shows where to implement the real API calls. This avoids embedding secrets in the repo.

        val url = "https://api.example.com/gemini/generate" // Placeholder - document replacement
        val json = "{\"prompt\":${escapeJson(prompt)} }"
        val body: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return try {
            val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }
            if (!response.isSuccessful) {
                GeminiResult.Error("Gemini API error: HTTP ${response.code}")
            } else {
                val respBody = response.body?.string().orEmpty()
                // Minimal parsing - in a real implementation parse JSON properly
                GeminiResult.Success(respBody)
            }
        } catch (e: IOException) {
            Log.e("GeminiClient", "Network error", e)
            GeminiResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    private fun escapeJson(s: String) = "\"" + s.replace("\"", "\\\"") + "\""
}
