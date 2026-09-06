package com.aperonix.ai.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aperonix.ai.ai.GeminiClient
import com.aperonix.ai.conversation.AppDatabase
import com.aperonix.ai.conversation.ConversationEntity
import com.aperonix.ai.memory.MemoryRepository
import com.aperonix.ai.settings.SettingsRepository
import com.aperonix.ai.voice.SpeechRecognizerManager
import com.aperonix.ai.voice.TextToSpeechManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    private val settingsRepo = SettingsRepository(application.applicationContext)
    private val memoryRepo = MemoryRepository(application.applicationContext)

    init {
        speechManager.initialize()
    }

    fun onPermissionGranted() {
        // Called once permission granted: apply user settings and greet
        viewModelScope.launch {
            try {
                val voice = settingsRepo.voice.first()
                val rate = settingsRepo.speechRate.first() ?: 1.0f
                val pitch = settingsRepo.pitch.first() ?: 1.0f

                ttsManager.setSpeechRate(rate)
                ttsManager.setPitch(pitch)
                if (!voice.isNullOrBlank() && voice != "Default") {
                    ttsManager.selectVoiceByName(voice)
                }

                // Friendly greeting
                _uiState.value = UiState.Speaking
                ttsManager.speak("Hello, I'm Aperonix. How can I help you?") {
                    _uiState.value = UiState.Idle
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Initialization error")
            }
        }
    }

    fun startListening() {
        _uiState.value = UiState.Listening
        speechManager.startListening { result ->
            viewModelScope.launch {
                _uiState.value = UiState.Thinking
                try {
                    // Save user message
                    db.conversationDao().insert(ConversationEntity(role = "user", content = result, timestamp = System.currentTimeMillis()))
                    // Call Gemini (demo)
                    when (val res = geminiClient.generateText(result)) {
                        is com.aperonix.ai.ai.GeminiResult.Success -> {
                            val text = res.text
                            // Save assistant message
                            db.conversationDao().insert(ConversationEntity(role = "assistant", content = text, timestamp = System.currentTimeMillis()))
                            _uiState.value = UiState.Speaking
                            ttsManager.speak(text) {
                                _uiState.value = UiState.Idle
                            }
                            // Execute structured action if any (validated)
                            res.action?.let { action ->
                                val valid = com.aperonix.ai.actions.ActionValidator.validate(action)
                                if (valid) {
                                    com.aperonix.ai.actions.ActionExecutor.execute(getApplication(), action)
                                }
                            }
                        }
                        is com.aperonix.ai.ai.GeminiResult.Error -> {
                            _uiState.value = UiState.Error(res.message)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
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
