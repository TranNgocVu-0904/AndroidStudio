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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LogInScreen(changeMessage: (String) -> Unit = {}, networkService: NetworkService) {

    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }



    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        changeMessage("Đây là bottom bar của log in screen")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        TextField(
            value = token,
            onValueChange = {},
            label = { Text("Token") },
            placeholder = { Text("Enter text") },
            modifier = Modifier.semantics{contentDescription= "Token Input"}.fillMaxWidth(),
            readOnly = true
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.semantics { contentDescription = "Email Input" }.fillMaxWidth()
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
                            withContext(Dispatchers.IO){
                            try {
                                val result = networkService.generateToken(email = UserCredential(email))

                                //writeToken(context, result.token)
                                token = result.token

                                // Only show up the "Save" button when fulfilled En and Viet
                                changeMessage("The token has been received successfully")

                            }
                            catch (e: SQLiteConstraintException){
                                // changeMessage("Flash Cards are duplicated")
                                changeMessage("Flash card already exists in your database.")
                            }
                            catch (e: Exception){
                                changeMessage("Unexpected Error")
                            }}
                        }
                    },
                // enabled = vietnamese.isNotBlank() && english.isNotBlank(),
                enabled = true,

                modifier = Modifier.semantics {
                    contentDescription = "Enter"
                }
            )
            { Text("Enter") }
        }


    }
}