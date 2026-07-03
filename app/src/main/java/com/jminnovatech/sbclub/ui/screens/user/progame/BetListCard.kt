package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jminnovatech.sbclub.data.model.progame.ProBetItem

@Composable
fun BetListCard(

    bets: List<ProBetItem>,

    onDelete: (Int) -> Unit,

    onPlaceBet: () -> Unit

) {

    val total = bets.sumOf { it.amount }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF1E293B)

        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(

                "My Bets",

                color = Color.White,

                fontWeight = FontWeight.Bold,

                fontSize = 18.sp

            )

            Spacer(Modifier.height(12.dp))

            if (bets.isEmpty()) {

                Text(

                    "No Bet Added",

                    color = Color.Gray

                )

            } else {

                LazyColumn(

                    modifier = Modifier.heightIn(max = 250.dp)

                ) {

                    itemsIndexed(bets) { index, bet ->

                        Card(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),

                            colors = CardDefaults.cardColors(

                                containerColor = Color(0xFF0F172A)

                            )

                        ) {

                            Row(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Column(

                                    modifier = Modifier.weight(1f)

                                ) {

                                    Text(

                                        bet.number,

                                        color = Color.White,

                                        fontWeight = FontWeight.Bold,

                                        fontSize = 18.sp

                                    )

                                    Text(

                                        "₹ %.2f".format(bet.amount),

                                        color = Color(0xFF4ADE80)

                                    )

                                }

                                IconButton(

                                    onClick = {

                                        onDelete(index)

                                    }

                                ) {

                                    Icon(

                                        Icons.Default.Delete,

                                        null,

                                        tint = Color.Red

                                    )

                                }

                            }

                        }

                    }

                }

                Spacer(Modifier.height(15.dp))

                HorizontalDivider()

                Spacer(Modifier.height(12.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(

                        "Total",

                        color = Color.White,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(Modifier.weight(1f))

                    Text(

                        "₹ %.2f".format(total),

                        color = Color(0xFF22C55E),

                        fontWeight = FontWeight.Bold,

                        fontSize = 18.sp

                    )

                }

                Spacer(Modifier.height(18.dp))

                Button(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    onClick = onPlaceBet,

                    shape = RoundedCornerShape(14.dp),

                    enabled = bets.isNotEmpty()

                ) {

                    Icon(

                        Icons.Default.Send,

                        null

                    )

                    Spacer(Modifier.width(8.dp))

                    Text(

                        "PLACE BET"

                    )

                }

            }

        }

    }

}