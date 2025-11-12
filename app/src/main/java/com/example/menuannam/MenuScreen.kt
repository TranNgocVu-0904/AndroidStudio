package com.example.menuannam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuAnNam(onStudy: () -> Unit, onAdd: () -> Unit, onSearch: () -> Unit, changeMessage: (String) -> Unit = {}){

    changeMessage("Đây là bottom bar của menu screen")

    Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Button(onClick = {onStudy()}, modifier = Modifier.semantics{ contentDescription= "navigationToStudyScreen" }.size(width = 100.dp, height = 80.dp)) {
                Text("Study")
            }
            Button(onClick = {onAdd()}, modifier = Modifier.semantics{ contentDescription= "navigationToAddScreen" }.size(width = 100.dp, height = 80.dp)) {
                Text("Add")
            }
            Button(onClick = {onSearch()}, modifier = Modifier.semantics{ contentDescription= "navigationToSearchScreen" }.size(width = 100.dp, height = 80.dp)) {
                Text("Search")
            }
        }
    }
