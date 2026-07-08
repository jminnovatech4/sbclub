package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.progame.ProBetItem

@Composable
fun BetPanel(

    gameCode: String,

    wallet: Double,

    betList: MutableList<ProBetItem>,

    onPlaceBet: () -> Unit

) {

    var showBetDialog by remember {

        mutableStateOf(false)

    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF111827)

        )

    ) {

        Column(

            modifier = Modifier.padding(18.dp)

        ) {

            Text(

                "My Bets",

                style = MaterialTheme.typography.titleMedium,

                color = Color.White,

                fontWeight = FontWeight.Bold

            )

            Spacer(Modifier.height(16.dp))

            if (betList.isEmpty()) {

                Text(

                    "No Bet Added",

                    color = Color.Gray

                )

            } else {

                betList.forEachIndexed { index, bet ->

                    BetRow(

                        bet = bet,

                        onDelete = {

                            betList.removeAt(index)

                        }

                    )

                }

            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    showBetDialog = true

                }

            ) {

                Text("ADD NEW BET")

            }

            Spacer(Modifier.height(15.dp))

            val total = betList.sumOf {

                it.amount

            }

            Button(

                modifier = Modifier

                    .fillMaxWidth()

                    .height(56.dp),

                enabled =

                    betList.isNotEmpty()

                            &&

                            total <= wallet,

                onClick = {

                    onPlaceBet()

                }

            ) {

                Text(

                    "PLACE BET ₹ %.2f".format(total)

                )

            }

        }

    }

    BetEntryDialog(

        show = showBetDialog,

        gameCode = gameCode,

        wallet = wallet,

        onDismiss = {

            showBetDialog = false

        },

        onAdd = { number, amount ->

            val exists = betList.any {

                it.number == number

            }

            if (!exists) {

                betList.add(

                    ProBetItem(

                        number,

                        amount

                    )

                )

            }

        }

    )

}