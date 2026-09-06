package com.aperonix.ai.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

data class TtsVoiceInfo(val name: String, val locale: Locale)

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized

    private var tts: TextToSpeech? = null
    private val availableVoices = mutableListOf<TtsVoiceInfo>()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _initialized.value = true
            try {
                val voices = tts?.voices
                voices?.forEach { v ->
                    // Avoid null locale
                    val locale = v.locale ?: Locale.getDefault()
                    availableVoices.add(TtsVoiceInfo(v.name, locale))
                }
            } catch (e: Exception) {
                Log.w("TTS", "voice enumeration failed", e)
            }
        } else {
            _initialized.value = false
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (tts == null) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "APERONIX_UTTERANCE")
        // Note: Utterance progress listener could be attached for onComplete
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    fun getAvailableVoices(): List<TtsVoiceInfo> = availableVoices
}
