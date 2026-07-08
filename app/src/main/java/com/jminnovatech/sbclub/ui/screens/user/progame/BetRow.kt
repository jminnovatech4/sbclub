package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.progame.ProBetItem

@Composable
fun BetRow(

    bet: ProBetItem,

    onDelete: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF1E293B)

        )

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)

        ) {

            Column(

                modifier = Modifier.weight(1f)

            ) {

                Text(

                    bet.number,

                    color = Color.White,

                    fontWeight = FontWeight.Bold

                )

                Text(

                    "₹ %.2f".format(bet.amount),

                    color = Color(0xFF22C55E)

                )

            }

            TextButton(

                onClick = onDelete

            ) {

                Text(

                    "DELETE",

                    color = Color.Red

                )

            }

        }

    }

}