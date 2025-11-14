package com.example.menuannam

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun FlashCardList(
    selectedItem: (FlashCard) -> Unit,
    flashCards: List<FlashCard>
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp)
                    .clickable(onClick = {
                        selectedItem(flashCard)
                    }
                    )
            ) {
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.englishCard.toString()) }
                Column(modifier = Modifier.padding(6.dp)) { Text(" = ") }
                Column(modifier = Modifier.padding(6.dp))
                { Text(flashCard.vietnameseCard.toString()) }
            }
        }
    }
}

@Composable
fun SearchScreen(
    changeMessage: (String) -> Unit = {},
    getAllFlashCards: suspend () -> List<FlashCard>,
    selectedItem: (FlashCard) -> Unit
) {
    var flashCards: List<FlashCard> by remember { mutableStateOf(emptyList<FlashCard>())}

    LaunchedEffect(Unit) {
        flashCards = getAllFlashCards()
    }

    changeMessage("Đây là bottom bar của search screen")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlashCardList(
            flashCards = flashCards,
            selectedItem = selectedItem
        )
        Button(
            onClick = {
                Log.d("My test", "click Button ")
            }
        )
        { Text("About") }
        Spacer(
            modifier = Modifier.size(16.dp)
        )
    }
}
