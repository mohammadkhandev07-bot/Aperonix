package com.aperonix.ai.voice

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    object Thinking : SpeechState()
    object Speaking : SpeechState()
    data class Error(val message: String) : SpeechState()
}

class SpeechRecognizerManager(private val context: Context) {
    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state: StateFlow<SpeechState> = _state

    private var speechRecognizer: SpeechRecognizer? = null

    private var listener: ((result: String) -> Unit)? = null

    fun initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _state.value = SpeechState.Listening
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _state.value = SpeechState.Thinking
                }

                override fun onError(error: Int) {
                    _state.value = SpeechState.Error("Recognition error: $error")
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull().orEmpty()
                    _state.value = SpeechState.Thinking
                    listener?.invoke(text)
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Could surface partial results if desired
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            _state.value = SpeechState.Error("Speech recognition not available on this device")
        }
    }

    fun startListening(onResult: (String) -> Unit) {
        listener = onResult
        if (speechRecognizer == null) initialize()
        val intent = RecognizerIntent().apply {
            action = RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
        _state.value = SpeechState.Listening
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w("SpeechRecognizerMngr", "stopListening failed", e)
        }
        _state.value = SpeechState.Idle
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        _state.value = SpeechState.Idle
    }
}
