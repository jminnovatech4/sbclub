package com.jminnovatech.sbclub.ui.screens.admin.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.ResultPreviewResponse

@Composable
fun PreviewCard(
    response: ResultPreviewResponse
) {

    val preview = response.preview

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Number")

                Text(preview.number)

            }

            Spacer(
                Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Players")

                Text(preview.players.toString())

            }

            Spacer(
                Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Bet Amount")

                Text("₹${preview.bet_amount}")

            }

            Spacer(
                Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Payable")

                Text("₹${preview.payable}")

            }

            Spacer(
                Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text("Profit")

                Text("₹${preview.profit}")

            }

        }

    }

}