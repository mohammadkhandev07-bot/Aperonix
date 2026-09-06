package com.aperonix.ai.conversation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConversationDao {
    @Insert
    suspend fun insert(entity: ConversationEntity): Long

    @Query("SELECT * FROM conversation ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<ConversationEntity>

    @Query("DELETE FROM conversation")
    suspend fun clear()
}
