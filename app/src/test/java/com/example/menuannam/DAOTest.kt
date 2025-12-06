import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.menuannam.FlashCard
import com.example.menuannam.FlashCardDao
import com.example.menuannam.MenuDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.jvm.java


@RunWith(RobolectricTestRunner::class)
class DaoTest {

    private lateinit var db: MenuDatabase
    private lateinit var flashCardDao: FlashCardDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MenuDatabase::class.java
        ).build()
        flashCardDao = db.flashCardDao()
    }

    @After
    fun close() {
        db.close()
    }

    @Test
    fun insertFlashCardSuccessful() {
        val flashCard =
            FlashCard(
                uid = 0,
                englishCard = "test_english",
                vietnameseCard = "test_vietnamese"
            )

        runBlocking {
            flashCardDao.insertAll(flashCard)
        }

        /*
        // Version 1
        val item: FlashCard
        runBlocking {
            item = flashCardDao.findByCards("test_english", "test_vietnamese")
        }
        assertEquals(flashCard.englishCard, item.englishCard)
        assertEquals(flashCard.vietnameseCard, item.vietnameseCard)
        */

        // Version 2
        val item: FlashCard?
        runBlocking {
            item = flashCardDao.findByCards("test_english", "test_vietnamese")
        }
        assertEquals(flashCard.englishCard, item!!.englishCard)
        assertEquals(flashCard.vietnameseCard, item!!.vietnameseCard)
    }

    @Test
    fun insertFlashCardUnSuccessful() {
        val flashCard =
            FlashCard(
                uid = 0,
                englishCard = "test_english",
                vietnameseCard = "test_english"
            )

        runBlocking {
            flashCardDao.insertAll(flashCard)
        }

        var error = false
        runBlocking {
            try {
                flashCardDao.insertAll(flashCard)
            } catch (e: SQLiteConstraintException) {
                error = true
            }
        }
        assertEquals(true, error)
    }
    /* Delete */
    @Test
    fun deleteExistingFlashCard() {
        val flashCard =
            FlashCard(
                uid = 0,
                englishCard = "test_english",
                vietnameseCard = "test_vietnamese"
            )

        var flashCardsBefore: List<FlashCard>
        runBlocking {
            flashCardsBefore = flashCardDao.getAll()
        }
        runBlocking{
            flashCardDao.insertAll(flashCard)
            flashCardDao.deleteByCardPair("test_english",
                vietnamese = "test_vietnamese")
        }
        var flashCardsAfter: List<FlashCard>
        runBlocking {
            flashCardsAfter = flashCardDao.getAll()
        }
        assertEquals(flashCardsBefore, flashCardsAfter)
    }


    @Test
    fun deleteNonExistingFlashCard() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MenuDatabase::class.java
        ).build()
        flashCardDao = db.flashCardDao()

        val flashCard =
            FlashCard(
                uid = 0,
                englishCard = "test_english",
                vietnameseCard = "test_vietnamese"
            )

        var flashCardsBefore: List<FlashCard>
        runBlocking {
            flashCardDao.insertAll(flashCard)
            flashCardsBefore = flashCardDao.getAll()
        }
        runBlocking {
            flashCardDao.deleteByCardPair(
                "test_english_1",
                vietnamese = "test_vietnamese_1"
            )
        }
        var flashCardsAfter: List<FlashCard>
        runBlocking {
            flashCardsAfter = flashCardDao.getAll()
        }
        assertEquals(flashCardsBefore, flashCardsAfter)
    }
    /* Similar for the other 2 cases */
}
