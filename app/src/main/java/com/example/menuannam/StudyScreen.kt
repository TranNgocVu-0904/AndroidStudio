package com.example.menuannam

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(changeMessage: (String) -> Unit = {},getRandomLesson: suspend (Int) -> List<FlashCard>) {

    // List 3 flashcards trong lesson
    var lessonCards by remember { mutableStateOf<List<FlashCard>>(emptyList()) }

    // Index của flashcard đang học
    var currentIndex by remember {  mutableStateOf(0) }

    // true = đang hiển thị English, false = đang hiển thị Vietnamese
    var showEnglish by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        try {
            val cards = getRandomLesson(3)
            lessonCards = cards
            if (cards.isEmpty()) {
                changeMessage("There are no flash cards in your database.")
            } else {
                // reset trạng thái về card đầu, English trước
                currentIndex = 0
                showEnglish = true
                changeMessage("New lesson generated with ${cards.size} cards.")
            }
        } catch (e: Exception) {
            changeMessage("Unexpected error while generating lesson.")
        }
    }


    Column (
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement =  Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    )
    {
        // Nếu lesson không rỗng thì hiển thị card hiện tại
        if (lessonCards.isNotEmpty()) {
            val currentCard = lessonCards[currentIndex]

            val textToShow =
                if (showEnglish)
                    currentCard.englishCard.orEmpty()
                else
                    currentCard.vietnameseCard.orEmpty()

            OutlinedButton(
                    onClick = {showEnglish  = !showEnglish},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = textToShow, style = MaterialTheme.typography.titleLarge)
                }
            if (!showEnglish) {
                Button(
                    onClick = {
                        if (currentIndex == lessonCards.lastIndex){


                            lessonCards = lessonCards.shuffled()
                            currentIndex = 0


                        }
                        else {
                            currentIndex++
                        }
                        showEnglish = true

                    },
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = "Next"
                    }
                ) {
                    Text("Next",style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}