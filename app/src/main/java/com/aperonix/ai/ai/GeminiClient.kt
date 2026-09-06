package com.aperonix.ai.ai

import android.util.Log
import com.aperonix.ai.actions.AssistantAction
import com.aperonix.ai.actions.AssistantAction.OpenApp
import com.aperonix.ai.actions.AssistantAction.OpenUrl
import com.aperonix.ai.actions.AssistantAction.OpenSettings
import com.aperonix.ai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class GeminiResult {
    data class Success(val text: String, val action: AssistantAction? = null) : GeminiResult()
    data class Error(val message: String) : GeminiResult()
}

class GeminiClient(private val httpClient: OkHttpClient = defaultClient()) {

    companion object {
        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Helper used by tests to extract action from a JSON response string
        fun extractActionFromResponse(responseText: String): AssistantAction? {
            try {
                val jo = JSONObject(responseText)
                if (jo.has("action")) {
                    val actionObj = jo.getJSONObject("action")
                    val type = actionObj.optString("type")
                    when (type) {
                        "OPEN_APP" -> {
                            val target = actionObj.optString("target")
                            if (target.isNotBlank()) return OpenApp(target)
                        }
                        "OPEN_URL" -> {
                            val url = actionObj.optString("target")
                            if (url.isNotBlank()) return OpenUrl(url)
                        }
                        "OPEN_SETTINGS" -> return OpenSettings
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiClient", "extractActionFromResponse failed", e)
            }
            return null
        }
    }

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    suspend fun generateText(prompt: String): GeminiResult {
        if (apiKey.isBlank()) {
            return GeminiResult.Error("Gemini API key missing. Configure GEMINI_API_KEY in local.properties.")
        }

        // NOTE: This repository intentionally uses a placeholder endpoint.
        // Replace `url` with the real Gemini REST endpoint and adjust request body accordingly.
        val url = "https://api.example.com/v1/generate" // TODO: replace with real endpoint
        val payload = JSONObject()
        payload.put("prompt", prompt)
        payload.put("system", AperonixSystemPrompt.PROMPT)

        val body = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Accept", "application/json")
            .build()

        return try {
            val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }
            val code = response.code
            val respBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                when (code) {
                    401 -> GeminiResult.Error("Unauthorized. Check GEMINI_API_KEY.")
                    429 -> GeminiResult.Error("Rate limited. Please try again later.")
                    else -> GeminiResult.Error("Gemini API error: HTTP $code")
                }
            } else {
                // Try to parse JSON response for structured action + text
                try {
                    val jo = JSONObject(respBody)
                    val text = when {
                        jo.has("text") -> jo.optString("text")
                        jo.has("output") -> jo.optString("output")
                        else -> respBody
                    }
                    val action = extractActionFromResponse(respBody)
                    GeminiResult.Success(text = text, action = action)
                } catch (e: Exception) {
                    // Not JSON or unexpected format - return raw
                    GeminiResult.Success(text = respBody)
                }
            }
        } catch (e: IOException) {
            Log.e("GeminiClient", "Network error", e)
            GeminiResult.Error("Network error: ${e.localizedMessage}")
        }
    }
}
