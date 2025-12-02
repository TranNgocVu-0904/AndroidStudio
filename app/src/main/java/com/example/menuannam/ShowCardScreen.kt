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
//    cardId: Int,
//    getFlashCardById: suspend (Int) -> FlashCard?,
//    deleteFlashCard: suspend (FlashCard) -> Unit,
//    navigateBack: () -> Unit,
//    changeMessage: (String) -> Unit,
//    updateFlashCard: suspend (FlashCard) -> Unit

    english: String,
    vietnamese: String,
    getFlashCardByPair: suspend (String, String) -> FlashCard?,
    deleteFlashCardByPair: suspend (FlashCard) -> Unit,
    navigateBack: () -> Unit,
    changeMessage: (String) -> Unit,
    updateFlashCard: suspend (FlashCard) -> Unit
) {
    var card by remember { mutableStateOf<FlashCard?>(null) }

    val scope = rememberCoroutineScope()

    var englishText by remember { mutableStateOf("") }
    var vietnameseText by remember { mutableStateOf("") }

    // Load card từ DB khi vào màn hình
    //    LaunchedEffect(cardId) {
    //        val loaded = getFlashCardById(cardId)
    //        card = loaded
    //        if (loaded != null) {
    //            englishText = loaded.englishCard ?: ""
    //            vietnameseText = loaded.vietnameseCard ?: ""
    //        }
    //    }

    LaunchedEffect(english, vietnamese) {
        val loaded = getFlashCardByPair(english, vietnamese)
        card = loaded
        if (loaded != null) {
            englishText = loaded.englishCard ?: ""
            vietnameseText = loaded.vietnameseCard ?: ""
        }
    }

    if (card == null) {
        Text("Card not found")
        return
    }

    val currentCard = card!!

    Column {

        TextField(
            value = englishText,
            onValueChange = {englishText = it}, // read-only nên bỏ trống
            enabled = true,
            label = { Text(stringResource(R.string.English_Label)) },
            placeholder = { Text("Enter text") }
        )

        TextField(
            value = vietnameseText,
            onValueChange = {vietnameseText = it},
            enabled = true,
            label = { Text(stringResource(R.string.Vietnamese_Label)) }
        )

        Button(onClick = {
            scope.launch {
                val updatedCard = currentCard.copy(
                    englishCard = englishText,
                    vietnameseCard = vietnameseText
                )
                updateFlashCard(updatedCard)
                changeMessage("Card updated")
                navigateBack()
            }
        }
        ){
            Text("Save")
        }

        // Nếu bạn vẫn muốn giữ nút Delete riêng:
        Button(onClick = {
            scope.launch {
                deleteFlashCardByPair(currentCard)
                changeMessage("Card deleted")
                navigateBack()
            }
        }
        ){
            Text("Delete")
        }
    }
}