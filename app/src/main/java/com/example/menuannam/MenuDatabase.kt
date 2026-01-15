package com.example.menuannam

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FlashCard::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
    companion object {
        @Volatile // Ensures visibility to all threads
        private var INSTANCE: MenuDatabase? = null

        fun getDatabase(context: Context): MenuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context to prevent memory leaks
                    MenuDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                INSTANCE = instance
                // Return instance
                instance
            }
        }
    }
}