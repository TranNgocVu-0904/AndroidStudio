package com.example.menuannam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuAnNam(
    onStudy: () -> Unit,
    onAdd: () -> Unit,
    onSearch: () -> Unit,
    changeMessage: (String) -> Unit = {},
    onLogIn: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val appContext = context.applicationContext

    Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Button(onClick = {onStudy()},  modifier = Modifier.width(100.dp).height(50.dp).semantics{ contentDescription= "navigationToStudyScreen" }) {
                Text("Study")
            }
            Button(onClick = {onAdd()},    modifier = Modifier.width(100.dp).height(50.dp).semantics{ contentDescription= "navigationToAddScreen" }) {
                Text("Add")
            }
            Button(onClick = {onSearch()}, modifier = Modifier.width(100.dp).height(50.dp).semantics{ contentDescription= "navigationToSearchScreen" }) {
                Text("Search")
            }
            Button(onClick = {onLogIn()},  modifier = Modifier.width(100.dp).height(50.dp).semantics{ contentDescription= "navigationToLogInScreen" }) {
                Text("Log In")
            }
            Button(
                modifier = Modifier
                    .width(100.dp).height(50.dp)
                    .semantics { contentDescription = "ExecuteLogout" } ,

                onClick = {
                    scope.launch {
                        appContext.dataStore.edit { preferences ->
                            preferences.remove(EMAIL)
                            preferences.remove(TOKEN)
                            changeMessage(preferences[EMAIL] ?: "")
                        }
                    }
                }) {
                Text(
                    "Log out",
                    modifier = Modifier.semantics { contentDescription = "Logout" }
                )
            }
        }
    }
