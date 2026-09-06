package com.aperonix.ai.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoryDao {
    @Insert
    suspend fun insert(memory: MemoryEntity): Long

    @Query("SELECT * FROM memory WHERE topic = :topic LIMIT 1")
    suspend fun findByTopic(topic: String): MemoryEntity?

    @Query("DELETE FROM memory")
    suspend fun clearAll()
}
