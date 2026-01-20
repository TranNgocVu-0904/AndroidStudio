package com.example.menuannam

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    // Callback to update the bottom message bar
    changeMessage: (String) -> Unit = {},
    // Suspend function to insert data into Room Database
    insertFlashCard: suspend (FlashCard) -> Unit
) {
    // UI State for Input Fields (rememberSaveable keeps text during screen rotation)
    var english by rememberSaveable { mutableStateOf("") }
    var vietnamese by rememberSaveable { mutableStateOf("") }

    // Local list to display cards added during this session
    val word = remember { mutableStateListOf<Pair<String, String>>() }

    // Scope required to launch suspend functions (database operations)
    val scope = rememberCoroutineScope()

    // Initial Setup: Update message when screen loads
    LaunchedEffect(Unit) {
        changeMessage("Please enter flashcard information.")
    }

    // Main Layout
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // --- INPUT FIELDS ---
        TextField(
            value = english,
            onValueChange = { english = it },
            label = { Text(stringResource(R.string.English_Label)) },
            placeholder = { Text("Enter text") },
            modifier = Modifier.semantics{contentDescription= "English Input"}.fillMaxWidth()
        )
        TextField(
            value = vietnamese,
            onValueChange = { vietnamese = it },
            label = { Text(stringResource(R.string.Vietnamese_Label)) },
            placeholder = { Text("Nhập nội dung") },
            modifier = Modifier.semantics { contentDescription = "Vietnamese Input" }.fillMaxWidth()
        )
        // --- ACTION BUTTONS ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
        )
        {
            Button(
                onClick = {
                    scope.launch {
                        try {
                            // Database Insert Operation
                            // Note: We use ID = 0 because Room usually auto-generates the ID
                            insertFlashCard(
                                FlashCard(
                                    0,
                                    englishCard = english,
                                    vietnameseCard = vietnamese
                                )
                            )
                            // Post-Insert UI Updates
                            // Only update UI list if inputs were not blank
                            if (vietnamese.isNotBlank() && english.isNotBlank()) {
                                word.add(english to vietnamese) // Add to temporary display list
                                english = ""    // Clear Input Field
                                vietnamese = "" // Clear Input Field
                            }
                            // Success Feedback
                            changeMessage("Flash card successfully added to your database.")
                        }
                        catch (e: SQLiteConstraintException){
                            // Error Handling: Duplicate Entry
                            // This catches unique constraint violations (e.g., card already exists)
                            changeMessage("Flash card already exists in your database.")
                        }
                        catch (e: Exception){
                            // Error Handling: Generic
                            changeMessage("Unexpected Error")
                        }
                    }
                },
                enabled = true,
                modifier = Modifier.semantics { contentDescription = "Save" }
            )
            { Text("Save") }
        }
        // --- HISTORY DISPLAY ---
        // Shows a list of words added in the current session
        Column {
            word.forEach { (english, vietnamese) ->
                Text("English: $english - Vietnamese: $vietnamese")
            }
        }
    }
}