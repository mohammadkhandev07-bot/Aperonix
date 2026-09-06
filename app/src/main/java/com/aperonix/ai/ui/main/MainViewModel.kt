package com.aperonix.ai.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aperonix.ai.ai.GeminiClient
import com.aperonix.ai.conversation.AppDatabase
import com.aperonix.ai.conversation.ConversationEntity
import com.aperonix.ai.voice.SpeechRecognizerManager
import com.aperonix.ai.voice.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Listening : UiState()
    object Thinking : UiState()
    object Speaking : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val speechManager = SpeechRecognizerManager(application.applicationContext)
    private val ttsManager = TextToSpeechManager(application.applicationContext)
    private val geminiClient = GeminiClient()
    private val db = AppDatabase.getInstance(application.applicationContext)

    init {
        speechManager.initialize()
    }

    fun startListening() {
        _uiState.value = UiState.Listening
        speechManager.startListening { result ->
            viewModelScope.launch {
                _uiState.value = UiState.Thinking
                // Save user message
                db.conversationDao().insert(ConversationEntity(role = "user", content = result, timestamp = System.currentTimeMillis()))
                // Call Gemini (demo)
                val res = geminiClient.generateText(result)
                when (res) {
                    is com.aperonix.ai.ai.GeminiResult.Success -> {
                        val text = res.text
                        // Save assistant message
                        db.conversationDao().insert(ConversationEntity(role = "assistant", content = text, timestamp = System.currentTimeMillis()))
                        _uiState.value = UiState.Speaking
                        ttsManager.speak(text)
                    }
                    is com.aperonix.ai.ai.GeminiResult.Error -> {
                        _uiState.value = UiState.Error(res.message)
                    }
                }
            }
        }
    }

    fun stopListening() {
        speechManager.stopListening()
        _uiState.value = UiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
        ttsManager.shutdown()
    }
}
