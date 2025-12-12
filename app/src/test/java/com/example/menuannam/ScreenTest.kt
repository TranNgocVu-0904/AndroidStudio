package com.example.menuannam

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

class DummyFlashCardDao : FlashCardDao {
    override suspend fun getAll(): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun findByCards(
        english: String,
        vietnamese: String
    ): FlashCard {
        return FlashCard(0, "", "")
    }

    override suspend fun getFlashCardByPair(
        english: String,
        vietnamese: String
    ): FlashCard? {
        TODO("Not yet implemented")
    }

    override suspend fun insertAll(vararg flashCard: FlashCard) {
    }

    override suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard> {
        TODO("Not yet implemented")
    }

    override suspend fun getFlashCardById(id: Int): FlashCard? {
        TODO("Not yet implemented")
    }

    override suspend fun updateFlashCardByPair(
        englishOld: String,
        vietnameseOld: String,
        englishNew: String,
        vietnameseNew: String
    ) {

    }

    override suspend fun getRandomFlashCards(size: Int): List<FlashCard> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByCardPair(english: String, vietnamese: String) {
        TODO("Not yet implemented")
    }

    override suspend fun searchFlashCardByPair(
        english: String,
        vietnamese: String
    ): List<FlashCard> {
        TODO("Not yet implemented")
    }
}

class DummyNetworkService : NetworkService {
    override suspend fun generateToken(
        url: String,
        email: UserCredential
    ): Token {
        // Không gọi mạng thật, trả về token giả
        return Token(token = "dummy-token")
    }
}

class DummyFlashCardDaoUnsuccessfulInsert : FlashCardDao {
    override suspend fun getAll(): List<FlashCard> {
        return emptyList<FlashCard>()
    }

    override suspend fun findByCards(
        english: String,
        vietnamese: String
    ): FlashCard {
        return FlashCard(0, "", "")
    }

    override suspend fun getFlashCardByPair(
        english: String,
        vietnamese: String
    ): FlashCard? {
        TODO("Not yet implemented")
    }

    override suspend fun insertAll(vararg flashCard: FlashCard) {
        throw SQLiteConstraintException()
    }

    override suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard> {
        TODO("Not yet implemented")
    }

    override suspend fun getFlashCardById(id: Int): FlashCard? {
        TODO("Not yet implemented")
    }

    override suspend fun updateFlashCardByPair(
        englishOld: String,
        vietnameseOld: String,
        englishNew: String,
        vietnameseNew: String
    ) {

    }

    override suspend fun getRandomFlashCards(size: Int): List<FlashCard> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByCardPair(english: String, vietnamese: String) {
    }

    override suspend fun searchFlashCardByPair(
        english: String,
        vietnamese: String
    ): List<FlashCard> {
        TODO("Not yet implemented")
    }
}

@RunWith(RobolectricTestRunner::class)
class ScreenTest {
    //This creates a shell activity that doesn't have any pre-set content, allowing your tests to call setContent() to set up the UI for the test.
    @get:Rule
    val composeTestRule = createComposeRule()

    // Navigator
    // type: Navigation
    @Test
    fun homeStartDestination() {
        val navController =
            TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val dummyFlashCardDao = DummyFlashCardDao()
        val dummyNetworkService = DummyNetworkService()
        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        assertEquals(true, navController.currentDestination?.hasRoute<MainRoute>())
    }

    @Test
    fun clickOnStudyCards() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService

            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToStudyScreen")
            .assertExists()
            .assertTextEquals("Study")
            .performClick();
        assertEquals(true, navController.currentDestination?.hasRoute<StudyRoute>())
    }

    @Test
    fun clickOnAddCard() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .assertExists()
            .assertTextEquals("Add")
            .performClick();
        assertEquals(true, navController.currentDestination?.hasRoute<AddRoute>())
    }

    @Test
    fun clickOnSearchCards() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToSearchScreen")
            .assertExists()
            .assertTextEquals("Search")
            .performClick();
        assertEquals(true, navController.currentDestination?.hasRoute<SearchRoute>())
    }

    @Test
    fun homeScreenRetained_afterConfigChange() {

        val stateRestorationTester = StateRestorationTester(composeTestRule)
        /*
        The StateRestorationTester class is used to test the state restoration for composable components without recreating activities.
        This makes tests faster and more reliable, as activity recreation is a complex process with multiple synchronization mechanisms:
        */
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        // Set content through the StateRestorationTester object.
        stateRestorationTester.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        // Simulate a config change.
        stateRestorationTester.emulateSavedInstanceStateRestore()
        assertEquals(true, navController.currentDestination?.hasRoute<MainRoute>())
    }

// AddCardScren
// type: Logic


    // AddCard
// type: navigation-back
    @Test
    fun clickOnAddCardAndBack() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .performClick();

        composeTestRule.onNodeWithContentDescription("navigateBack")
            .assertExists()
            .performClick();
        assertEquals(true, navController.currentDestination?.hasRoute<MainRoute>())
    }

    // AddCard
    @Test
    fun typeOnEnTextInput() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .performClick();

        val textInput = "house"

        //composeTestRule.onNodeWithContentDescription("enTextField")
        composeTestRule.onNodeWithContentDescription("English Input")
            .assertExists()
            .performTextInput(textInput)

        //composeTestRule.onNodeWithContentDescription("enTextField")
        composeTestRule.onNodeWithContentDescription("English Input")
            //.assertTextEquals("en", textInput)
            .assertTextEquals("English", textInput)
    }

    // AddCard
    @Test
    fun keepEnglishStringAfterRotation() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val stateRestorationTester = StateRestorationTester(composeTestRule)

        val dummyNetworkService = DummyNetworkService()

        stateRestorationTester.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .performClick();

        val textInput = "house"
        // composeTestRule.onNodeWithContentDescription("enTextField")
        composeTestRule.onNodeWithContentDescription("English Input")
            .assertExists()
            .performTextInput(textInput)

        // Simulate a config change.
        stateRestorationTester.emulateSavedInstanceStateRestore()
        // composeTestRule.onNodeWithContentDescription("enTextField")
        composeTestRule.onNodeWithContentDescription("English Input")
            //.assertTextEquals("en", textInput)
            .assertTextEquals("English", textInput)
    }

    @Test
    fun clickOnAddCardSuccessful() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDao()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .performClick();

        // composeTestRule.onNodeWithContentDescription("Add")
        composeTestRule.onNodeWithContentDescription("Save")
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Message")
            .assertExists()
            .assertTextEquals("Flash card successfully added to your database.")
    }


// AddCardScren
// type: Logic

    @Test
    fun clickOnAddCardUnSuccessful() {

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val dummyFlashCardDao = DummyFlashCardDaoUnsuccessfulInsert()

        val dummyNetworkService = DummyNetworkService()

        composeTestRule.setContent {
            AppNavigation(
                navigation = navController,
                flashCardDao = dummyFlashCardDao,
                networkService = dummyNetworkService
            )
        }
        composeTestRule.runOnUiThread {
            navController.navigate(MainRoute)
        }
        composeTestRule.onNodeWithContentDescription("navigationToAddScreen")
            .performClick();

        // composeTestRule.onNodeWithContentDescription("Add")
        composeTestRule.onNodeWithContentDescription("Save")
            .assertExists()
            .performClick()

        composeTestRule.onNodeWithContentDescription("Message")
            .assertExists()
            .assertTextEquals("Flash card already exists in your database.")
    }
}