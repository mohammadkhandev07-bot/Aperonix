package com.aperonix.ai.ui.memory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.aperonix.ai.memory.MemoryEntity
import com.aperonix.ai.memory.MemoryRepository
import kotlinx.coroutines.launch

@Composable
fun MemoryScreen(memoryRepository: MemoryRepository) {
    val scope = rememberCoroutineScope()
    val items by androidx.compose.runtime.produceState(initialValue = emptyList<MemoryEntity>(), key1 = memoryRepository) {
        value = kotlinx.coroutines.runBlocking { memoryRepository.getAllMemory() }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { scope.launch { memoryRepository.clearAll() } }) {
            Text(text = "Clear All Memory")
        }

        LazyColumn {
            items(items) { m ->
                Text(text = "${m.topic}: ${m.value}")
            }
        }
    }
}
