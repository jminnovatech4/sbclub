package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WalletSummary(

    wallet: Double,

    bet: Double

) {

    Card(

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF111827)

        )

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            horizontalArrangement = Arrangement.SpaceEvenly

        ) {

            WalletBox(

                "Wallet",

                "₹ %.2f".format(wallet),

                Color(0xFF22C55E)

            )

            WalletBox(

                "Bet",

                "₹ %.2f".format(bet),

                Color(0xFFF59E0B)

            )

            WalletBox(

                "Left",

                "₹ %.2f".format(wallet - bet),

                Color.White

            )

        }

    }

}

@Composable
private fun WalletBox(

    title: String,

    value: String,

    color: Color

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(

            title,

            color = Color.Gray

        )

        Spacer(Modifier.height(4.dp))

        Text(

            value,

            color = color,

            fontWeight = FontWeight.Bold

        )

    }

}