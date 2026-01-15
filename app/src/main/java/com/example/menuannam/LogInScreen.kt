package com.example.menuannam

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
import androidx.compose.runtime.rememberCoroutineScope
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
fun LogInScreen(
    // State is hoisted: 'email' comes from the parent composable
    email: String,
    changeMessage: (String) -> Unit = {},
    networkService: NetworkService,

    // Callback to navigate to the next screen (Token Screen) upon success
    onToken: () -> Unit,

    // Callback to update the email state in the parent
    onEmailChange: (String) -> Unit
) {
    // Coroutine scope for handling button clicks (Side Effects)
    val scope = rememberCoroutineScope()

    // Initial Setup: Set the bottom bar message when screen loads
    LaunchedEffect(Unit) {
        changeMessage("Please, introduce your email for login.")
    }

    // Main Layout
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // --- INPUT SECTION ---
        TextField(
            value = email,
            onValueChange = onEmailChange, // Delegates state update to parent
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.semantics { contentDescription = "Email Input" }.fillMaxWidth()
        )

        // --- ACTION BUTTON SECTION ---
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
                            // Network Request
                            // Switch to IO Dispatcher for network operations to avoid freezing UI
                            val result = withContext(Dispatchers.IO) {
                                networkService.generateToken(email = UserCredential(email))
                            }
                            // Handle Response
                            if (result.code == 200) {
                                changeMessage("Tokens have been sent")
                                onToken() // Navigate to next screen
                            } else {
                                changeMessage("Token sending failed: ${result.message}")
                            }
                        }
                        // Error Handling
                        catch (e: retrofit2.HttpException) {
                            changeMessage("HTTP Error: ${e.code()}")
                        }
                        catch (e: Exception){
                            changeMessage("Unexpected Error: ${e.message}")
                        }
                    }
                },
                enabled = true,
                modifier = Modifier.semantics {
                    contentDescription = "Enter"
                }
            )
            { Text("Enter") }
        }
    }
}