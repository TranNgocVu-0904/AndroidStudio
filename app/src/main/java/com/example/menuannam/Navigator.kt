package com.example.menuannam

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.emptyPreferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavigation(
    navigation: NavHostController,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
     /*
     ==========================================
     1. STATE MANAGEMENT
     ==========================================
     */

    // Top Bar State (Title & Back Button visibility)
    var title by rememberSaveable { mutableStateOf("Menu An Nam") }
    var showBack by rememberSaveable { mutableStateOf(false) }

    // State for the Bottom Bar message
    var message by rememberSaveable { mutableStateOf("") }

    // Context & DataStore (User Preferences)
    val context = LocalContext.current
    val prefs by context.dataStore.data.collectAsState(initial = emptyPreferences())

    // Extract user credentials
    val email = prefs[EMAIL].orEmpty()
    val token = prefs[TOKEN].orEmpty()

    // Helper to update bottom bar message with logging
    val changeMessage: (String) -> Unit = { newMsg ->
        Log.d("BottomBar", "changeMessage('$newMsg') called")
        message = newMsg
    }

    val setShowBack: (Boolean) -> Unit = { showBack = it }
    val setTitle: (String) -> Unit = { title = it }

     /*
     ==========================================
     2. NAVIGATION ACTIONS (Callbacks)
     ==========================================
     */

    // Simple navigation without arguments
    val toMain   = fun () { navigation.navigate(MainRoute)  { launchSingleTop = true } }
    val toStudy  = fun () { navigation.navigate(StudyRoute) { launchSingleTop = true } }
    val toAdd    = fun () { navigation.navigate(AddRoute)   { launchSingleTop = true } }
    val toSearch = fun () { navigation.navigate(SearchRoute){ launchSingleTop = true } }
    val toLogIn  = fun () { navigation.navigate(LogInRoute) { launchSingleTop = true } }

    // Navigation with arguments
    val toToken: (String) -> Unit = { email ->
        navigation.navigate(TokenRoute(email = email)) { launchSingleTop = true }
    }

    val toFlashCard: (String, Boolean, String, Boolean) -> Unit = { en, exactEn, vn, exactVn ->
        navigation.navigate(
            FlashCardRoute(
                english = en,
                enWord = exactEn,
                vietnamese = vn,
                vnWord = exactVn
            )
        ) { launchSingleTop = true }
    }

    val toCardSelected: (FlashCard) -> Unit = { card ->
        navigation.navigate(
            ShowCardRoute(
                english = card.englishCard ?: "",
                vietnamese = card.vietnameseCard ?: ""
            )
        ) { launchSingleTop = true }
    }

    val toEditCard: (FlashCard) -> Unit = { card ->
        navigation.navigate(
            EditCardRoute(
                englishOld = card.englishCard ?: "",
                vietnameseOld = card.vietnameseCard ?: ""
            )
        ) { launchSingleTop = true }
    }

    val navigateBack: () -> Unit = { navigation.navigateUp() }

     /*
     ==========================================
     3. DATABASE OPERATIONS (Hoisted Functions)
     ==========================================
     */

    val insertFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.insertAll(flashCard)
    }

    val getAllFlashCards: suspend () -> List<FlashCard> = {
        flashCardDao.getAll()
    }

    val updateFlashCardByPair: suspend (String, String, String, String) -> Unit =
        { oldEn, oldVn, newEn, newVn ->
            flashCardDao.updateFlashCardByPair(oldEn, oldVn, newEn, newVn)
        }

    val getFlashCardByPair: suspend (String, String) -> FlashCard? = { en, vn ->
        flashCardDao.getFlashCardByPair(en, vn)
    }

    val getRandomLesson: suspend (Int) -> List<FlashCard> = { limit ->
        flashCardDao.getRandomFlashCards(limit)
    }

    val deleteFlashCardByPair: suspend (FlashCard) -> Unit = { card ->
        flashCardDao.deleteByCardPair(
            english = card.englishCard ?: "",
            vietnamese = card.vietnameseCard ?: ""
        )
    }

    val searchFlashCardByPair: suspend (String, String) -> List<FlashCard> = { en, vn ->
        flashCardDao.searchFlashCardByPair(en, vn)
    }

    val getFilteredFlashCards: suspend (String, Boolean, String, Boolean) -> List<FlashCard> =
        { en, exactEn, vn, exactVn ->
            flashCardDao.getFilteredFlashCards(en, exactEn, vn, exactVn)
        }

     /*
     ==========================================
     4. MAIN UI STRUCTURE (Scaffold + NavHost)
     ==========================================
     */

    Scaffold(
        topBar = {
            TopBarComponent (
                title = title,
                // Only pass the back function if showBack is true
                showBack = if (showBack)
                    navigateBack
                else
                    null
            )
        },
        bottomBar = {
            BottomBarComponent(message = message)
        }
    ) { innerPadding ->

        NavHost(
            navController = navigation,
            startDestination = MainRoute,
            modifier = Modifier.padding(innerPadding)
        ) {

            // --- HOME SCREEN ---
            composable<MainRoute> {
                // Update TopBar configuration
                LaunchedEffect(Unit) {
                    setShowBack(false)
                    setTitle("Menu An Nam")
                }

                // Update BottomBar based on login status
                LaunchedEffect(email) {
                    if (email.isNotBlank()) {
                        changeMessage(email)
                    } else {
                        changeMessage("")
                    }
                }

                MenuAnNam(
                    changeMessage = changeMessage,
                    onStudy = toStudy,
                    onAdd = toAdd,
                    onSearch = toSearch,
                    onLogIn= toLogIn
                )
            }

            // --- STUDY SCREEN ---
            composable <StudyRoute>  {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Study Screen")
                }

                StudyScreen(
                    changeMessage = changeMessage,
                    getRandomLesson = getRandomLesson,
                    networkService = networkService,
                    email = email,
                    token = token
                )
            }

            // --- ADD CARD SCREEN ---
            composable <AddRoute> {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Add Screen")
                }
                AddScreen(
                    changeMessage = changeMessage,
                    insertFlashCard = insertFlashCard
                )
            }

            // --- SEARCH SCREEN ---
            composable <SearchRoute> {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Search Screen")
                }
                SearchScreen(
                    changeMessage = changeMessage,
                    onFlashCard = toFlashCard,
                )
            }
            // --- FLASH CARD LIST (Result of Search) ---
            composable <FlashCardRoute> { backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Flash Card Screen")
                }

                // Retrieve Type-Safe Arguments
                val args: FlashCardRoute = backStackEntry.toRoute()

                FlashCardScreen(
                    changeMessage = changeMessage,
                    deleteFlashCardByPair = deleteFlashCardByPair,
                    getFilteredFlashCards = getFilteredFlashCards,
                    selectedItem =  toCardSelected,
                    editItem = toEditCard,
                    english = args.english,
                    vietnamese = args.vietnamese,
                    isExactEnglish = args.enWord,
                    isExactVietnamese = args.vnWord
                )
            }

            // --- SHOW DETAILS SCREEN ---
            composable<ShowCardRoute> { backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Show Card Screen")
                }

                val args: ShowCardRoute = backStackEntry.toRoute()

                ShowCardScreen(
                    changeMessage = changeMessage,
                    getFlashCardByPair = getFlashCardByPair,
                    deleteFlashCardByPair = deleteFlashCardByPair,
                    navigateBack = navigateBack,
                    english = args.english,
                    vietnamese = args.vietnamese,
                )
            }

            // --- EDIT CARD SCREEN ---
            composable<EditCardRoute>{ backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Edit Card Screen")
                }

                val args: EditCardRoute = backStackEntry.toRoute()

                EditCardScreen(
                    changeMessage = changeMessage,
                    getFlashCardByPair = getFlashCardByPair,
                    updateFlashCardByPair = updateFlashCardByPair,
                    networkService = networkService,
                    englishOld = args.englishOld,
                    vietnameseOld = args.vietnameseOld,
                    email = email,
                    token = token
                )
            }

            // --- LOGIN SCREEN ---
            composable <LogInRoute>  {
                var emailInput by rememberSaveable { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Log In Screen")
                }

                LogInScreen(
                    changeMessage = changeMessage,
                    email = emailInput,
                    onToken = { emailFromLogIn ->
                        toToken(emailFromLogIn)
                    },
                    networkService = networkService,
                )
            }

            // --- TOKEN / OTP SCREEN ---
            composable <TokenRoute> { backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Token Screen")
                }

                val args: TokenRoute = backStackEntry.toRoute()

                TokenScreen(
                    changeMessage = changeMessage,
                    onMain = toMain,
                    email = args.email
                )
            }
        }
    }
}