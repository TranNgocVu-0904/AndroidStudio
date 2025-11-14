package com.example.menuannam

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "FlashCards", indices = [Index(
    value = ["english_card", "vietnamese_card"],
    unique = true
)])
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)

@Dao
interface FlashCardDao {
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE uid = :id LIMIT 1")
    suspend fun getById(id: Int): FlashCard?

    @Query("SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
            "vietnamese_card LIKE :vietnamese LIMIT 1")

    suspend fun findByCards(english: String, vietnamese: String): FlashCard
    @Insert
    suspend fun insertAll(vararg flashCard: FlashCard)

    @Delete
    suspend fun delete(flashCard: FlashCard)
}

