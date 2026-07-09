package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
@Composable
fun GameTopBar(

    title:String,

    wallet:Double,

    onBack:()->Unit

){

    Surface(

        shadowElevation = 8.dp,

        color = Color.Transparent

    ){

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(

                    Brush.horizontalGradient(

                        listOf(

                            Color(0xFF2563EB),

                            Color(0xFF1E40AF)

                        )

                    )

                )

                .padding(

                    horizontal = 16.dp,

                    vertical = 14.dp

                )

        ){

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ){

                FilledIconButton(

                    onClick = onBack,

                    shape = CircleShape,

                    colors = IconButtonDefaults.filledIconButtonColors(

                        containerColor = Color.White.copy(.18f)

                    )

                ){

                    Icon(

                        Icons.Default.ArrowBack,

                        null,

                        tint = Color.White

                    )

                }

                Spacer(Modifier.width(15.dp))

                Column(

                    modifier = Modifier.weight(1f)

                ){

                    Text(

                        text = title,

                        color = Color.White,

                        fontSize = 23.sp,

                        fontWeight = FontWeight.Bold

                    )

                    Text(

                        "Play & Win",

                        color = Color.White.copy(.75f),

                        fontSize = 12.sp

                    )

                }

                Card(

                    shape = RoundedCornerShape(50),

                    colors = CardDefaults.cardColors(

                        containerColor = Color.White

                    )

                ){

                    Row(

                        modifier = Modifier.padding(

                            horizontal = 14.dp,

                            vertical = 10.dp

                        ),

                        verticalAlignment = Alignment.CenterVertically

                    ){

                        Icon(

                            Icons.Default.AccountBalanceWallet,

                            null,

                            tint = Color(0xFF2563EB)

                        )

                        Spacer(Modifier.width(8.dp))

                        Text(

                            text = "₹ %.2f".format(wallet),

                            color = Color(0xFF0F172A),

                            fontWeight = FontWeight.Bold,

                            fontSize = 16.sp

                        )

                    }

                }

            }

        }

    }

}