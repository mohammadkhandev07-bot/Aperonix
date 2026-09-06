package com.aperonix.ai.conversation

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aperonix.ai.memory.MemoryEntity
import com.aperonix.ai.memory.MemoryDao
import android.content.Context

@Database(entities = [ConversationEntity::class, MemoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        private const val DB_NAME = "aperonix.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
