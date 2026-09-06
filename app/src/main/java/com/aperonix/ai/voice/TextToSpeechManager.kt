package com.aperonix.ai.voice

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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

    private var currentOnComplete: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _initialized.value = true
            try {
                val voices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) tts?.voices else null
                voices?.forEach { v ->
                    val locale = v.locale ?: Locale.getDefault()
                    availableVoices.add(TtsVoiceInfo(v.name, locale))
                }

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // no-op
                    }

                    override fun onDone(utteranceId: String?) {
                        currentOnComplete?.invoke()
                    }

                    override fun onError(utteranceId: String?) {
                        currentOnComplete?.invoke()
                    }
                })

            } catch (e: Exception) {
                Log.w("TTS", "voice enumeration failed", e)
            }
        } else {
            _initialized.value = false
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (tts == null) return
        currentOnComplete = onComplete
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "APERONIX_UTTERANCE")
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

    fun selectVoiceByName(name: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false
        try {
            val voices = tts?.voices ?: return false
            val match = voices.firstOrNull { it.name == name }
            if (match != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts?.voice = match
                    return true
                }
            }
        } catch (e: Exception) {
            Log.w("TTS", "selectVoiceByName failed", e)
        }
        return false
    }
}
