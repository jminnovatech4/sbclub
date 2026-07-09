package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.progame.BetHistory

@Composable
fun RunningBetCard(

    bet: BetHistory

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF111827)

        )

    ) {

        Column(

            modifier = Modifier.padding(10.dp)

        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {

                Box(

                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            Color(0xFFF59E0B),
                            CircleShape
                        )

                )

                Spacer(Modifier.width(10.dp))

                Text(

                    "Bet #${bet.id}",

                    fontWeight = FontWeight.Bold,

                    color = Color.White

                )

                Spacer(Modifier.weight(1f))

                Surface(

                    shape = RoundedCornerShape(50),

                    color = Color(0x33F59E0B)

                ) {

                    Row(

                        modifier = Modifier.padding(

                            horizontal = 10.dp,

                            vertical = 5.dp

                        ),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            Icons.Default.HourglassTop,

                            null,

                            tint = Color(0xFFF59E0B),

                            modifier = Modifier.size(16.dp)

                        )

                        Spacer(Modifier.width(5.dp))

                        Text(

                            bet.status.uppercase(),

                            color = Color(0xFFF59E0B)

                        )

                    }

                }

            }

            Spacer(Modifier.height(5.dp))

            bet.items.forEach { item ->

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(

                        item.bet_number,

                        modifier = Modifier.weight(1f),

                        fontWeight = FontWeight.Bold,

                        color = Color.White

                    )

                    Text(

                        "₹ %.2f".format(item.amount),

                        color = Color(0xFF22C55E),

                        fontWeight = FontWeight.Bold

                    )

                }

            }

            HorizontalDivider(

                modifier = Modifier.padding(vertical = 6.dp),

                color = Color(0xFF293548)

            )

            Row(

                modifier = Modifier.fillMaxWidth()

            ) {

                Text(

                    "Total",

                    color = Color.Gray

                )

                Spacer(Modifier.weight(1f))

                Text(

                    "₹ %.2f".format(bet.total_amount),

                    fontWeight = FontWeight.Bold,

                    color = Color.White

                )

            }

        }

    }

}