package com.example.menuannam

import androidx.compose.foundation.layout.Column

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
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

@Composable
fun ShowCardScreen(
    cardId: Int,
    getFlashCardById: suspend (Int) -> FlashCard?,
    deleteFlashCard: suspend (FlashCard) -> Unit,
    navigateBack: () -> Unit,
    changeMessage: (String) -> Unit
) {
    var card by remember { mutableStateOf<FlashCard?>(null) }

    val scope = rememberCoroutineScope()

    // Load card từ DB khi vào màn hình
    LaunchedEffect(cardId) {
        card = getFlashCardById(cardId)
    }

    if (card == null) {
        Text("Card not found")
        return
    }

    val currentCard = card!!

    Column {
        TextField(
            value = currentCard.englishCard ?: "",
            onValueChange = {}, // read-only nên bỏ trống
            enabled = true,
            label = { Text(stringResource(R.string.English_Label)) },
            placeholder = { Text("Enter text") }
        )

        TextField(
            value = currentCard.vietnameseCard ?: "",
            onValueChange = {},
            enabled = true,
            label = { Text(stringResource(R.string.Vietnamese_Label)) }
        )


        Button(onClick = {
            scope.launch {
                deleteFlashCard(currentCard)
                changeMessage("Card deleted")
                navigateBack()
            }
        }) {
            Text("Delete")
        }
    }
}