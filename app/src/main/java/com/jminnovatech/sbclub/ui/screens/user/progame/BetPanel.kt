package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827)

        )

    ) {

        Column(

            modifier = Modifier.padding(15.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "My Bets",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Surface(
                    color = Color(0x2222C55E),
                    shape = RoundedCornerShape(50)
                ) {

                    Text(
                        "${betList.size} Bets",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 5.dp
                        ),
                        color = Color(0xFF22C55E)
                    )

                }

            }

            Spacer(Modifier.height(16.dp))

            if (betList.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "🎯",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "No Bets Added Yet",
                        color = Color.Gray
                    )

                    Text(
                        "Tap below to add your first bet",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodySmall
                    )

                }

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

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                ),

                onClick = {
                    showBetDialog = true
                }

            ) {

                Text(
                    "＋ ADD NEW BET"
                )

            }

            Spacer(Modifier.height(15.dp))

            val total = betList.sumOf {

                it.amount

            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFF293548)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Total Bet",
                    color = Color.Gray
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "₹ %.2f".format(total),
                    color = Color(0xFF22C55E),
                    fontWeight = FontWeight.Bold
                )

            }
            if (total > wallet) {

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "⚠ Insufficient Wallet Balance",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(5.dp))
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