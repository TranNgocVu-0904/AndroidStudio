package com.example.menuannam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ShowCardScreen(
    // Parameters passed from Navigation
    english: String,
    vietnamese: String,

    // Database and Navigation Callbacks
    getFlashCardByPair: suspend (String, String) -> FlashCard?,
    deleteFlashCardByPair: suspend (FlashCard) -> Unit,
    navigateBack: () -> Unit,
    changeMessage: (String) -> Unit
) {
    // 1. STATE: Holds the card data fetched from the DB
    var card by remember { mutableStateOf<FlashCard?>(null) }

    val scope = rememberCoroutineScope()

    // 2. INITIALIZATION: Set the bottom bar message
    // Wrapped in LaunchedEffect to run only once when screen loads
    LaunchedEffect(Unit) {
        changeMessage("Press the delete button to delete the flashcard. ")
    }

    // 3. FETCH DATA: Load the specific card based on the English/Vietnamese pair
    LaunchedEffect(english, vietnamese) {
        card = getFlashCardByPair(english, vietnamese)
    }

    // 4. LOADING STATE: Wait until data is fetched
    if (card == null) {
        Text("Loading...")
        return
    }

    // Force unwrap is safe here because we checked for null above
    val currentCard = card!!

    // 5. MAIN UI LAYOUT
    Column (
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){

        // --- READ-ONLY INPUT FIELDS ---

        // English Field
        TextField(
            value = currentCard.englishCard ?: "",
            onValueChange = {}, // Empty lambda because it's read-only
            readOnly = true,    // User cannot type here
            label = { Text(stringResource(R.string.English_Label)) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "English card" }
        )

        // Vietnamese Field
        TextField(
            value = currentCard.vietnameseCard?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.Vietnamese_Label)) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Vietnamese card" }
        )

        // --- DELETE ACTION ---
        Button(
            onClick = {
                scope.launch {
                    // 1. Delete from Database
                    deleteFlashCardByPair(currentCard)

                    // 2. Notify User
                    changeMessage("Card deleted")

                    // 3. Go back to previous screen
                    navigateBack()
                }
            },
            modifier = Modifier.padding(8.dp)
        ){
            Text("Delete")
        }
    }
}