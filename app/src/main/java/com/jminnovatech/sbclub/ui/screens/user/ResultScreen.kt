package com.jminnovatech.sbclub.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen() {

    val dummyResults = listOf("5","2","9","1","7")

    LazyColumn {

        items(dummyResults.size) {

            Card(Modifier.padding(8.dp)) {
                Text("Result: ${dummyResults[it]}", Modifier.padding(16.dp))
            }
        }
    }
}