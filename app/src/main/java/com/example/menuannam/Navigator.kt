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
@Composable
fun AppNavigation(navigation: NavHostController) {
    // --- Bottom bar state ---
    var message by rememberSaveable { mutableStateOf("") }
    val changeMessage: (String) -> Unit = { message = it }

    // --- Điều hướng (chọn 1 cách) ---
    val toStudy  = { navigation.navigate("Study")  { launchSingleTop = true } }
    val toAdd    = { navigation.navigate("Add")    { launchSingleTop = true } }
    val toSearch = { navigation.navigate("Search") { launchSingleTop = true } }
    val navigateBack: () -> Unit = { navigation.navigateUp() }

    // --- Title & Back theo route ---
    val backstackEntry by navigation.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route
    val title = when (currentRoute) {
        "Study"  -> "Study Screen"
        "Add"    -> "Add Screen"
        "Search" -> "Search Screen"
        "Main", null -> "Menu An Nam"
        else -> "Menu An Nam"
    }
    var showBack by rememberSaveable { mutableStateOf(false) }
    val setShowBack: (Boolean) -> Unit = { showBack = it }


    Scaffold(
        topBar = {
            TopBarComponent(
                title = title,
                showBack = if (showBack) ({ navigation.navigateUp() }) else null
            )
        },
        bottomBar = { BottomBarComponent(message = message) }
    ) { innerPadding ->
        NavHost(navigation, "Main", Modifier.padding(innerPadding)) {
            composable("Main") {
                LaunchedEffect(Unit) { setShowBack(false) }
                MenuAnNam(
                    onStudy = toStudy,
                    onAdd = toAdd,
                    onSearch = toSearch,
                    changeMessage = changeMessage
                )
            }
            composable("Study")  {
                // Gợi ý: trong StudyScreen dùng LaunchedEffect để set message
                LaunchedEffect(Unit) { setShowBack(true) }
                StudyScreen(
                    changeMessage = changeMessage
                )
            }
            composable("Add") {
                LaunchedEffect(Unit) { setShowBack(true) }
                AddScreen(
                    changeMessage = changeMessage
                )
            }
            composable("Search") {
                LaunchedEffect(Unit) { setShowBack(true) }
                SearchScreen(
                    changeMessage = changeMessage
                )
            }
        }
    }
}
