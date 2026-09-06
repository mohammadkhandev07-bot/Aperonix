package com.aperonix.ai.memory

import android.content.Context
import com.aperonix.ai.conversation.AppDatabase

class MemoryRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val memoryDao = db.memoryDao()

    suspend fun saveMemory(topic: String, value: String) {
        val now = System.currentTimeMillis()
        val existing = memoryDao.findByTopic(topic)
        if (existing != null) {
            // Insert a new record as a simple approach (could be an update operation)
            memoryDao.insert(MemoryEntity(topic = topic, value = value, createdAt = now, updatedAt = now))
        } else {
            memoryDao.insert(MemoryEntity(topic = topic, value = value, createdAt = now, updatedAt = now))
        }
    }

    suspend fun getMemory(topic: String): MemoryEntity? {
        return memoryDao.findByTopic(topic)
    }

    suspend fun getAllMemory(): List<MemoryEntity> = memoryDao.getAll()

    suspend fun clearAll() {
        memoryDao.clearAll()
    }
}
