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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute

@Composable
fun AppNavigation(navigation: NavHostController,  flashCardDao: FlashCardDao) {
    // --- Bottom bar state ---
    var message by rememberSaveable { mutableStateOf("") }
    val changeMessage: (String) -> Unit = { message = it }

    // --- Điều hướng (chọn 1 cách) ---
    val toStudy  = { navigation.navigate(StudyRoute)  { launchSingleTop = true } }
    val toAdd    = { navigation.navigate(AddRoute)    { launchSingleTop = true } }
    val toSearch = { navigation.navigate(SearchRoute) { launchSingleTop = true } }
    val navigateBack: () -> Unit = { navigation.navigateUp() }

    // --- Title & Back theo route ---

    var title by rememberSaveable { mutableStateOf("Menu An Nam") }

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

    val getFlashCardById: suspend (Int) -> FlashCard? = { id ->
        flashCardDao.getById(id)
    }

    val deleteFlashCard: suspend (FlashCard) -> Unit = { card ->
        flashCardDao.delete(card)
    }

    val onCardSelected: (FlashCard) -> Unit = { card ->
        navigation.navigate(ShowCardRoute(card.uid))
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
                }
                MenuAnNam(
                    onStudy = toStudy,
                    onAdd = toAdd,
                    onSearch = toSearch,
                    changeMessage = changeMessage
                )
            }
            composable <StudyRoute>  {
                // Gợi ý: trong StudyScreen dùng LaunchedEffect để set message
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Study Screen")
                }
                StudyScreen(
                    changeMessage = changeMessage
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
                    selectedItem =  onCardSelected
                )
            }
            composable<ShowCardRoute> { backStackEntry ->
                LaunchedEffect(Unit) {
                    setShowBack(true)
                    setTitle("Show Card Screen")
                }

                // Lấy argument theo kiểu type-safe
                val args: ShowCardRoute = backStackEntry.toRoute()

                ShowCardScreen(
                    cardId = args.cardId,
                    getFlashCardById = getFlashCardById,
                    deleteFlashCard = deleteFlashCard,
                    navigateBack = navigateBack,
                    changeMessage = changeMessage
                )
            }
        }
    }
}


