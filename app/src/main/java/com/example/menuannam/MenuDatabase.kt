package com.example.menuannam

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FlashCard::class], version = 1)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
}