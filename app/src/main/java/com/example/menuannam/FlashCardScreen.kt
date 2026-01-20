package com.example.menuannam

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardList(
    editItem: (FlashCard) -> Unit,
    selectedItem: (FlashCard) -> Unit,
    flashCards: List<FlashCard>,
    onDelete: (FlashCard) -> Unit
) {
    // Scrollable list container
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        // Iterate through each flashcard in the list
        items(
            items = flashCards,
            key = { flashCard -> flashCard.uid } // Unique key for optimization
        ) { flashCard ->
            // --- Card Container ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- Content Section (Clickable) ---
                Row(
                    modifier = Modifier
                        .weight(1f) // Takes up remaining space
                        .clickable { selectedItem(flashCard) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = flashCard.englishCard.orEmpty(),
                        modifier = Modifier.padding(6.dp)
                    )

                    Text(
                        text = " = ",
                        modifier = Modifier.padding(6.dp)
                    )

                    Text(
                        text = flashCard.vietnameseCard.orEmpty(),
                        modifier = Modifier.padding(6.dp)
                    )
                }
                // --- Action Buttons (Edit / Delete) ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Button
                    Text(
                        text = "Edit",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { editItem(flashCard) }
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                    // Delete Button
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onDelete(flashCard) }
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.error,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
fun FlashCardScreen(
    changeMessage: (String) -> Unit = {},
    selectedItem: (FlashCard) -> Unit,
    deleteFlashCardByPair: suspend (FlashCard) -> Unit,
    getFilteredFlashCards: suspend (String, Boolean, String, Boolean) -> List<FlashCard>,
    editItem: (FlashCard) -> Unit,
    english: String,
    vietnamese: String,
    isExactEnglish: Boolean,
    isExactVietnamese: Boolean,
) {
    // State to hold the list of cards
    var flashCards: List<FlashCard> by remember { mutableStateOf(emptyList()) }

    val scope = rememberCoroutineScope()

    // --- Helper Function: Fetch and Update List ---
    val updateCardList: suspend () -> Unit = {
        // Query the database
        val result = getFilteredFlashCards(english, isExactEnglish, vietnamese, isExactVietnamese)

        // Update state
        flashCards = result

        // Update bottom status message
        if (result.isEmpty()){
            changeMessage("No cards were found. The database is empty!")
        }
        else {
            changeMessage("${result.size} available word card(s)")
        }
    }

    // --- Side Effect: Load Data ---
    // Runs when screen loads OR when search inputs (english/vietnamese) change
    LaunchedEffect(english, vietnamese) {
        updateCardList()
    }

    // --- Main UI Layout ---
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // CASE 1: List is Empty -> Show Error Message
        if (flashCards.isEmpty()) {
            Text(
                text = "No cards were found. Please try again!",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(20.dp)
            )
        }

        // CASE 2: List has Data -> Show the List
        else {
            FlashCardList(
                flashCards = flashCards,
                selectedItem = selectedItem,
                editItem = editItem,
                onDelete = { card ->
                    // Handle Delete logic
                    scope.launch {
                        deleteFlashCardByPair(card)
                        updateCardList() // Refresh list after deleting
                    }
                }
            )
        }
    }
}