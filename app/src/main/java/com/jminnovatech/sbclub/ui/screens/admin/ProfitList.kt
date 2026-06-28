package com.jminnovatech.sbclub.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.api.NumberReport

@Composable
fun ProfitList(numbers: List<NumberReport>) {

    LazyColumn {

        items(numbers) { item ->

            val color = when(item.color){
                "red" -> Color.Red
                "green" -> Color(0xFF4CAF50)
                else -> Color.Blue
            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    Text("No: ${item.number}")

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "₹${item.payout}",
                        color = color
                    )
                }
            }
        }
    }
}