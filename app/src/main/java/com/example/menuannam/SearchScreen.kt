package com.example.menuannam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

// --- HELPER COMPOSABLE ---
// A custom Checkbox row that allows clicking the entire row to toggle
@Composable
fun CheckBox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    changeMessage: (String) -> Unit = {},
    // Callback to navigate to FlashCard list with search params
    onFlashCard: (String, Boolean, String, Boolean) -> Unit
) {

    // ==========================================
    // 1. UI STATE (Saved across configuration changes)
    // ==========================================

    var english by rememberSaveable { mutableStateOf("") }
    var vietnamese by rememberSaveable { mutableStateOf("") }

    // Toggles for "Exact Match" vs "Contains" search
    var isEnglishChecked by rememberSaveable { mutableStateOf(false) }
    var isVietnameseChecked by rememberSaveable { mutableStateOf(false) }

    // ==========================================
    // 2. INITIALIZATION
    // ==========================================

    // Show instruction message when screen loads
    LaunchedEffect(Unit) {
        changeMessage("Please tick the checkbox to search for the exact word")
    }

    // ==========================================
    // 3. MAIN LAYOUT
    // ==========================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // --- ENGLISH SEARCH ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Checkbox for Exact Match
            CheckBox(
                isChecked = isEnglishChecked,
                onCheckedChange = { isEnglishChecked = it }
            )

            // Input Field
            TextField(
                value = english,
                onValueChange = { english = it },
                label = { Text(stringResource(R.string.English_Label)) },
                placeholder = { Text("Enter text") },
                modifier = Modifier
                    .semantics { contentDescription = "English Input" }
                    .weight(1f) // Occupy remaining width
            )
        }

        // --- VIETNAMESE SEARCH ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            // Checkbox for Exact Match
            CheckBox(
                isChecked = isVietnameseChecked,
                onCheckedChange = { isVietnameseChecked = it }
            )

            // Input Field
            TextField(
                value = vietnamese,
                onValueChange = { vietnamese = it },
                label = { Text(stringResource(R.string.Vietnamese_Label)) },
                placeholder = { Text("Nhập nội dung") },
                modifier = Modifier
                    .semantics { contentDescription = "Vietnamese Input" }
                    .weight(1f)
            )
        }

        // --- ACTION BUTTON ---
        Button(
            onClick = {
                // Pass all 4 parameters to the navigation callback
                onFlashCard(
                    english,
                    isEnglishChecked,
                    vietnamese,
                    isVietnameseChecked
                )
            },
            enabled = true,
            modifier = Modifier.semantics {
                contentDescription = "Search"
            }
        )
        { Text("Search") }
    }
}