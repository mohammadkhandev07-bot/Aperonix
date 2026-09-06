package com.aperonix.ai.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "aperonix_settings")

object SettingsKeys {
    val VOICE = stringPreferencesKey("voice")
    val SPEECH_RATE = floatPreferencesKey("speech_rate")
    val PITCH = floatPreferencesKey("pitch")
    val MEMORY_ENABLED = booleanPreferencesKey("memory_enabled")
}

class SettingsRepository(private val context: Context) {
    private val ds = context.dataStore

    val voice: Flow<String?> = ds.data.map { it[SettingsKeys.VOICE] }
    val speechRate: Flow<Float?> = ds.data.map { it[SettingsKeys.SPEECH_RATE] }
    val pitch: Flow<Float?> = ds.data.map { it[SettingsKeys.PITCH] }
    val memoryEnabled: Flow<Boolean?> = ds.data.map { it[SettingsKeys.MEMORY_ENABLED] }

    suspend fun setVoice(value: String) {
        ds.edit { it[SettingsKeys.VOICE] = value }
    }

    suspend fun setSpeechRate(value: Float) {
        ds.edit { it[SettingsKeys.SPEECH_RATE] = value }
    }

    suspend fun setPitch(value: Float) {
        ds.edit { it[SettingsKeys.PITCH] = value }
    }

    suspend fun setMemoryEnabled(value: Boolean) {
        ds.edit { it[SettingsKeys.MEMORY_ENABLED] = value }
    }
}
