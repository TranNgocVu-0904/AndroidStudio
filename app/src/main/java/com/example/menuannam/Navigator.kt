package com.example.menuannam

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavigation(navigation: NavHostController,  flashCardDao: FlashCardDao, networkService: NetworkService) {
    // --- Bottom bar state ---
    var message by rememberSaveable { mutableStateOf("") }
    val changeMessage: (String) -> Unit = { message = it }


    // --- Navigation ---
    val toStudy  = { navigation.navigate(StudyRoute)  { launchSingleTop = true } }
    val toAdd    = { navigation.navigate(AddRoute)    { launchSingleTop = true } }
    val toSearch = { navigation.navigate(SearchRoute) { launchSingleTop = true } }
    val toLogIn = { navigation.navigate(LogInRoute) { launchSingleTop = true } }
    val navigateBack: () -> Unit = { navigation.navigateUp() }

    // --- Title & Back based on route ---

    var title by rememberSaveable {
        mutableStateOf("Menu An Nam")
    }

    var showBack by rememberSaveable {
        mutableStateOf(false)
    }

    val setShowBack: (Boolean) -> Unit = {
        showBack = it
    }

    val setTitle: (String) -> Unit = { title = it }

    val insertFlashCard: suspend (FlashCard) -> Unit = {
            flashCard -> flashCardDao.insertAll(flashCard)
    }

    val getAllFlashCards: suspend () -> List<FlashCard> = {
        flashCardDao.getAll()
    }

    val updateFlashCardByPair: suspend (
        String, String, String, String
    ) -> Unit = { oldEn, oldVn, newEn, newVn ->
        flashCardDao.updateFlashCardByPair(oldEn, oldVn, newEn, newVn)
    }

    /*
    val getFlashCardById: suspend (Int) -> FlashCard? = { id ->
        flashCardDao.getFlashCardById(id)
    }
    */

    val getFlashCardByPair: suspend (String, String) -> FlashCard? = { en, vn ->
        flashCardDao.getFlashCardByPair(en, vn)
    }

    val getRandomLesson: suspend (Int) -> List<FlashCard> = { limit ->
        flashCardDao.getRandomFlashCards(limit)
    }

    /*
    val deleteFlashCard: suspend (FlashCard) -> Unit = { card ->
            flashCardDao.delete(card)
        }
    */

    val deleteFlashCardByPair: suspend (FlashCard) -> Unit = { card ->
        flashCardDao.deleteByCardPair(
            english = card.englishCard ?: "",
            vietnamese = card.vietnameseCard ?: ""
        )
    }

    val searchFlashCardByPair: suspend (String, String) -> List<FlashCard> = { en, vn ->
        flashCardDao.searchFlashCardByPair(en, vn)
    }

    /*
    val onCardSelected: (FlashCard) -> Unit = { card ->
        navigation.navigate(ShowCardRoute(card.uid))
    }
     */

    val onCardSelected: (FlashCard) -> Unit = { card ->
        navigation.navigate(
            ShowCardRoute(
                english = card.englishCard ?: "",
                vietnamese = card.vietnameseCard ?: ""
            )
        )
    }
    /*
    val onEditCard: (FlashCard) -> Unit = { card ->
        navigation.navigate(
            EditCardRoute(
                english = card.englishCard ?: "",
                vietnamese = card.vietnameseCard ?: ""
            )
        )
    }
    */

    val onEditCard: (FlashCard) -> Unit = { card ->
        navigation.navigate(
            EditCardRoute(
                englishOld = card.englishCard ?: "",
                vietnameseOld = card.vietnameseCard ?: ""
            )
        )
    }

    Scaffold(
        topBar = {
            TopBarComponent (
                title = title,
                showBack = if (showBack) (
                        navigateBack
                )
                else null
            )
        },
        bottomBar = {
            BottomBarComponent(
                message = message
            )
        }
    ) { innerPadding ->
        NavHost(navigation, MainRoute, Modifier.padding(innerPadding)) {
            composable<MainRoute> {
                LaunchedEffect(Unit) {
                    setShowBack(false)
                    setTitle("Menu An Nam")
                }
                MenuAnNam(
                    onStudy = toStudy,
                    onAdd = toAdd,
                    onSearch = toSearch,
                    onLogIn= toLogIn,
                    changeMessage = changeMessage
                )
            }
            composable <StudyRoute>  {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Study Screen")
                }
                StudyScreen(
                    changeMessage = changeMessage,
                    getRandomLesson = getRandomLesson
                )
            }
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
            composable <SearchRoute> {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Search Screen")
                }
                SearchScreen(
                    changeMessage = changeMessage,
                    getAllFlashCards = getAllFlashCards,
                    deleteFlashCardByPair = deleteFlashCardByPair,
                    selectedItem =  onCardSelected,
                    editItem = onEditCard,
                    searchFlashCardByPair = searchFlashCardByPair
                )
            }
            composable<ShowCardRoute> { backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Show Card Screen")
                }

                // Take argument for type-safe navigation
                val args: ShowCardRoute = backStackEntry.toRoute()
                /*
                ShowCardScreen(
                    cardId = args.cardId,
                    getFlashCardById = getFlashCardById,
                    deleteFlashCard = deleteFlashCard,
                     updateFlashCard = updateFlashCard,
                    navigateBack = navigateBack,
                    changeMessage = changeMessage
                )
                 */
                ShowCardScreen(
                    english = args.english,
                    vietnamese = args.vietnamese,
                    getFlashCardByPair = getFlashCardByPair,
                    deleteFlashCardByPair = deleteFlashCardByPair,
                    navigateBack = navigateBack,
                    changeMessage = changeMessage
                )
            }
            composable<EditCardRoute>{ backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Edit Card Screen")
                }
                // Take argument for type-safe navigation
                val args: EditCardRoute = backStackEntry.toRoute()

                EditCardScreen(
                    englishOld = args.englishOld,
                    vietnameseOld = args.vietnameseOld,
                    getFlashCardByPair = getFlashCardByPair,
                    updateFlashCardByPair = updateFlashCardByPair,
                    navigateBack = navigateBack,
                    changeMessage = changeMessage
                )
            }
            composable <LogInRoute>  {
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Log In Screen")
                }
                LogInScreen(
                    changeMessage = changeMessage,
                    networkService = networkService
                )
            }
        }
    }
}


