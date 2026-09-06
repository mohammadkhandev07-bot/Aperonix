package com.aperonix.ai.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.aperonix.ai.conversation.ConversationEntity
import com.aperonix.ai.conversation.ConversationDao
import kotlinx.coroutines.launch

@Composable
fun ConversationScreen(conversationDao: ConversationDao) {
    val scope = rememberCoroutineScope()
    // For simplicity, fetch recent items in a simple effect (in real code use ViewModel + Flow)
    // Here we assume caller provides the DAO and will call on a background thread where needed.
    val items by androidx.compose.runtime.produceState(initialValue = emptyList<ConversationEntity>(), key1 = conversationDao) {
        value = kotlinx.coroutines.runBlocking { conversationDao.recent(50) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { scope.launch { conversationDao.clear() } }) {
            Text(text = "Clear Conversation")
        }

        LazyColumn {
            items(items) { item ->
                Text(text = "[${item.role}] ${item.content}")
            }
        }
    }
}
