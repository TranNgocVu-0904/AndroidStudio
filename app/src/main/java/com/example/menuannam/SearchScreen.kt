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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard -> flashCard.uid }
        ) { flashCard ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { editItem(flashCard) }
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )

                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onDelete(flashCard) }
                            .padding(4.dp), // cho dễ bấm hơn
                        // có thể thêm style cho giống link hoặc nút
                        color = MaterialTheme.colorScheme.error,
                        textDecoration = TextDecoration.Underline
                    )


                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    changeMessage: (String) -> Unit = {},
    getAllFlashCards: suspend () -> List<FlashCard>,
    selectedItem: (FlashCard) -> Unit,
    searchFlashCardByPair: suspend (String, String) -> List<FlashCard>,
    deleteFlashCardByPair: suspend (FlashCard) -> Unit,
    editItem: (FlashCard) -> Unit
) {
    var flashCards: List<FlashCard> by remember { mutableStateOf(emptyList()) }

    var vnText by rememberSaveable { mutableStateOf("") }
    var enText by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val updateCardList: suspend () -> Unit = {
        flashCards = searchFlashCardByPair(enText, vnText)
    }

    // Load list khi vào màn hình
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
            selectedItem =  selectedItem,
            editItem = editItem,
            onDelete = { card ->
                scope.launch {
                    deleteFlashCardByPair(card)
                    updateCardList()
                }
            }
        )
        Button(
            onClick = {
                Log.d("My test", "click Button ")
            }
        ) {
            Text("About")
        }
        Spacer(
            modifier = Modifier.size(16.dp)
        )
    }
}
