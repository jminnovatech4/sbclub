package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BetHistoryItem(

    val number: String,

    val amount: Double,

    val winAmount: Double,

    val isWin: Boolean

)

@Composable
fun ResultHistoryCard(

    scheduleId: Int,

    resultNumber: String?,

    history: List<BetHistoryItem> = emptyList(),

    onRefresh: () -> Unit

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF0F172A)

        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ) {

                Column(

                    modifier = Modifier.weight(1f)

                ) {

                    Text(

                        "Result History",

                        color = Color.White,

                        fontWeight = FontWeight.Bold,

                        fontSize = 18.sp

                    )

                    Text(

                        "Schedule #$scheduleId",

                        color = Color.Gray

                    )

                }

                IconButton(

                    onClick = onRefresh

                ) {

                    Icon(

                        Icons.Default.Refresh,

                        null,

                        tint = Color.White

                    )

                }

            }

            Spacer(Modifier.height(12.dp))

            Text(

                "Winning Number",

                color = Color.Gray

            )

            Spacer(Modifier.height(5.dp))

            Text(

                resultNumber ?: "---",

                color = Color(0xFF22C55E),

                fontWeight = FontWeight.Bold,

                fontSize = 24.sp

            )

            Spacer(Modifier.height(16.dp))

            if(history.isEmpty()){

                Text(

                    "No Bet Found",

                    color = Color.Gray

                )

            }else{

                LazyColumn{

                    items(history){ item ->

                        Card(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),

                            colors = CardDefaults.cardColors(

                                containerColor = Color(0xFF1E293B)

                            )

                        ){

                            Row(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ){

                                Column(

                                    modifier = Modifier.weight(1f)

                                ){

                                    Text(

                                        item.number,

                                        color = Color.White,

                                        fontWeight = FontWeight.Bold

                                    )

                                    Text(

                                        "₹ %.2f".format(item.amount),

                                        color = Color.LightGray

                                    )

                                }

                                if(item.isWin){

                                    Column(

                                        horizontalAlignment = Alignment.End

                                    ){

                                        Text(

                                            "WIN",

                                            color = Color.Green,

                                            fontWeight = FontWeight.Bold

                                        )

                                        Text(

                                            "₹ %.2f".format(item.winAmount),

                                            color = Color.Green

                                        )

                                    }

                                }else{

                                    Text(

                                        "LOSS",

                                        color = Color.Red,

                                        fontWeight = FontWeight.Bold

                                    )

                                }

                            }

                        }

                    }

                }

            }

        }

    }

}