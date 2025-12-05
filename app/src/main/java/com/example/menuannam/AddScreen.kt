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
fun AddScreen(changeMessage: (String) -> Unit = {}, insertFlashCard: suspend (FlashCard) -> Unit) {

    var english by rememberSaveable { mutableStateOf("") }
    var vietnamese by rememberSaveable { mutableStateOf("") }

    val word = remember { mutableStateListOf<Pair<String, String>>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        changeMessage("Đây là bottom bar của add screen")
    }

    Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
            )
            {
                Button(
                    onClick =
                        {
                            scope.launch {
                                try {
                                    insertFlashCard(
                                        FlashCard(
                                            0,
                                            englishCard = english,
                                            vietnameseCard = vietnamese
                                        )
                                    )
                                    if (vietnamese.isNotBlank() && english.isNotBlank()) {
                                        word.add(english to vietnamese) //Make a pair of words
                                        english = "" // Clear the text field
                                        vietnamese = "" // Clear the text field
                                    }
                                    // Only show up the "Save" button when fulfilled En and Viet
                                    changeMessage("Flash card successfully added to your database.")
                                }
                                catch (e: SQLiteConstraintException){
                                    // changeMessage("Flash Cards are duplicated")
                                    changeMessage("Flash card already exists in your database.")
                                }
                                catch (e: Exception){
                                    changeMessage("Unexpected Error")
                                }
                            }
                        },
                    // enabled = vietnamese.isNotBlank() && english.isNotBlank(),
                    enabled = true,

                    modifier = Modifier.semantics {
                        contentDescription = "Save"
                    }
                )
                { Text("Save") }
            }

            Column {
                word.forEach { (english, vietnamese) ->
                    Text("English: $english - Vietnamese: $vietnamese")
                }
            }
        }
    }

/*
@Composable
fun AddScreen(onBack: () -> Unit) {

    var enWord = ""

    var vnWord = ""

    //var enWord by remember { mutableStateOf("") }

    //var vnWord by remember { mutableStateOf("") }

    //var enWord by rememberSaveable { mutableStateOf("") }

    //var vnWord by rememberSaveable { mutableStateOf("") }

    Column() {

        TextField(

            value = enWord,

            onValueChange = { enWord = it },

            modifier = Modifier.semantics{contentDescription = "English String"},

            label = { Text("en") }

        )

        TextField(

            value = vnWord,

            onValueChange = { vnWord = it },

            label = { Text("vn") }

        )

        Button(onClick = {

            Log.d(

                "TEST", "Adding a card with words: "

                        + enWord + " and " + vnWord

            )

        }) {

            Text("Add")

        }

    }

}
 */