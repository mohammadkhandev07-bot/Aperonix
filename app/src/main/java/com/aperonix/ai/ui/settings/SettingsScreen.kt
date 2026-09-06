package com.aperonix.ai.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aperonix.ai.settings.SettingsRepository
import kotlinx.coroutines.launch
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.aperonix.ai.ai.GeminiClient
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import com.aperonix.ai.voice.TextToSpeechManager
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsRepo: SettingsRepository) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }

    val availableVoices = remember { mutableStateOf(listOf("Default")) }
    LaunchedEffect(ttsManager) {
        // Wait a moment for TTS to initialize and populate voices
        kotlinx.coroutines.delay(500)
        val list = ttsManager.getAvailableVoices().map { it.name }
        if (list.isNotEmpty()) availableVoices.value = list
    }

    val speechRate by settingsRepo.speechRate.collectAsState(initial = 1.0f)
    val pitch by settingsRepo.pitch.collectAsState(initial = 1.0f)
    val memoryEnabled by settingsRepo.memoryEnabled.collectAsState(initial = false)

    val snackbarHostState = remember { SnackbarHostState() }

    val expanded = remember { mutableStateOf(false) }
    val selectedVoice = remember { mutableStateOf(availableVoices.value.firstOrNull() ?: "Default") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Assistant", style = MaterialTheme.typography.titleMedium)
        Text(text = "Name: Aperonix")
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Voice")
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
            TextField(value = selectedVoice.value, onValueChange = {}, modifier = Modifier.fillMaxWidth(), readOnly = true)
            ExposedDropdownMenuDefaults.DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                availableVoices.value.forEach { v ->
                    DropdownMenuItem(text = { Text(v) }, onClick = {
                        selectedVoice.value = v
                        expanded.value = false
                        scope.launch {
                            settingsRepo.setVoice(v)
                            ttsManager.selectVoiceByName(v)
                        }
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Speech rate: ${String.format("%.2f", speechRate ?: 1.0f)}")
        Slider(value = speechRate ?: 1.0f, onValueChange = { v ->
            scope.launch { settingsRepo.setSpeechRate(v); ttsManager.setSpeechRate(v) }
        }, valueRange = 0.5f..1.5f)

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Pitch: ${String.format("%.2f", pitch ?: 1.0f)}")
        Slider(value = pitch ?: 1.0f, onValueChange = { v ->
            scope.launch { settingsRepo.setPitch(v); ttsManager.setPitch(v) }
        }, valueRange = 0.5f..1.5f)

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Test voice
            ttsManager.speak("Hello, I am Aperonix. This is a voice test.")
        }) {
            Text(text = "Test Voice")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "AI")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // perform a simple connection test using GeminiClient
            scope.launch {
                val client = GeminiClient()
                val result = client.generateText("Ping from Aperonix")
                when (result) {
                    is com.aperonix.ai.ai.GeminiResult.Success -> {
                        snackbarHostState.showSnackbar("Gemini responded (length ${result.text.length})")
                    }
                    is com.aperonix.ai.ai.GeminiResult.Error -> {
                        snackbarHostState.showSnackbar("Gemini error: ${result.message}")
                    }
                }
            }
        }) {
            Text(text = "Test Gemini Connection")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Memory")
        Spacer(modifier = Modifier.height(8.dp))
        RowSection(
            onToggle = { enabled -> scope.launch { settingsRepo.setMemoryEnabled(enabled) } },
            checked = memoryEnabled ?: false
        )

        Spacer(modifier = Modifier.height(16.dp))

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun RowSection(onToggle: (Boolean) -> Unit, checked: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Enable memory")
        Spacer(modifier = Modifier.height(8.dp))
        Switch(checked = checked, onCheckedChange = { onToggle(it) })
    }
}
