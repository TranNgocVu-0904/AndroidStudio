package com.example.menuannam

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.serialization.Serializable

// ======================= Entity =======================

@Entity(
    tableName = "FlashCards",
    indices = [
        Index(
            value = ["english_card", "vietnamese_card"],
            unique = true
        )
    ]
)

@Serializable
data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)

// ======================= DAO =======================

@Dao
interface FlashCardDao {

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int


    // ----------------------- Common / All -----------------------

    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    @Insert
    suspend fun insertAll(vararg flashCard: FlashCard)


    // ----------------------- Based on ID -----------------------

    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE uid = :id LIMIT 1")
    suspend fun getFlashCardById(id: Int): FlashCard?


    // ----------------------- Based on pair (EN/VN) -----------------------

    // Tìm theo cặp từ (dùng LIKE + LIMIT 1)
    @Query(
        "SELECT * FROM FlashCards " +
                "WHERE english_card LIKE :english AND vietnamese_card LIKE :vietnamese " +
                "LIMIT 1"
    )
    suspend fun findByCards(english: String, vietnamese: String): FlashCard

    // Search by pairs of words (compare =, do not LIKE)
    @Query(
        "SELECT * FROM FlashCards " +
                "WHERE english_card = :english AND vietnamese_card = :vietnamese " +
                "LIMIT 1"
    )
    suspend fun getFlashCardByPair(english: String, vietnamese: String): FlashCard?

    // Delete by word pair
    @Query(
        "DELETE FROM FlashCards " +
                "WHERE english_card = :english AND vietnamese_card = :vietnamese"
    )
    suspend fun deleteByCardPair(english: String, vietnamese: String)

    @Query("SELECT * FROM FlashCards " +
            "WHERE english_card LIKE '%' || :english || '%' " +
            "AND vietnamese_card LIKE '%' || :vietnamese || '%' "
    )

    suspend fun searchFlashCardByPair(english: String, vietnamese: String): List<FlashCard>

    @Query("UPDATE FlashCards " +
            "SET english_card = :englishNew, " +
            "vietnamese_card = :vietnameseNew " +
            "WHERE english_card = :englishOld " +
            "AND vietnamese_card = :vietnameseOld")
    suspend fun updateFlashCardByPair( englishOld: String, vietnameseOld: String, englishNew: String,  vietnameseNew: String )

    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getRandomFlashCards(size: Int): List<FlashCard>

    // Retrieves flashcards filtering by English and Vietnamese text, with toggleable exact/substring matching for each field.
    @Query(
        "SELECT * FROM FlashCards WHERE " +
                "(CASE WHEN :exactEn THEN english_card LIKE :en  " +
                "WHEN NOT :exactEn  THEN english_card LIKE '%' || :en || '%' END) " +
                "AND " +
                "(CASE WHEN :exactVn THEN vietnamese_card LIKE :vn " +
                "WHEN NOT :exactVn THEN vietnamese_card LIKE '%' || :vn || '%' END)"
    )
    suspend fun getFilteredFlashCards(en: String, exactEn: Boolean, vn: String, exactVn: Boolean): List<FlashCard>
}
