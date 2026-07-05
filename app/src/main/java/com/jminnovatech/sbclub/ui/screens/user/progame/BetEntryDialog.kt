package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetEntryDialog(

    show:Boolean,

    gameCode:String,

    wallet:Double,

    onDismiss:()->Unit,

    onAdd:(String,Double)->Unit

){

    if(!show) return

    var number by remember{ mutableStateOf("") }
    var amount by remember{ mutableStateOf("") }

    var error by remember{

        mutableStateOf("")

    }

    ModalBottomSheet(

        onDismissRequest = onDismiss,

        containerColor = Color(0xFF101826),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 60.dp, height = 6.dp)
                    .background(
                        Color.White.copy(.35f),
                        RoundedCornerShape(100)
                    )
            )
        }

    ){

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)

        ){

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF3B82F6)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ){

                    Icon(
                        Icons.Default.Dialpad,
                        null,
                        tint = Color.White
                    )

                }

                Spacer(Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ){

                    Text(
                        "Add New Bet",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        gameCode.uppercase(),
                        color = Color(0xFF60A5FA)
                    )

                }

                FilledIconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White.copy(.08f)
                    )
                ){

                    Icon(
                        Icons.Default.Close,
                        null,
                        tint = Color.White
                    )

                }

            }
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = number,
                onValueChange = {
                    number = it.filter { c -> c.isDigit() }
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF111827),
                    unfocusedContainerColor = Color(0xFF111827),
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))

            Text(

                text = BetValidator.error(gameCode),

                color = Color(0xFF94A3B8),

                fontSize = 12.sp

            )
            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = {
                    Text("Amount")
                },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF111827),
                    unfocusedContainerColor = Color(0xFF111827),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,

                    cursorColor = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))

            Text(

                text = "Minimum ₹10",

                color = Color(0xFF94A3B8),

                fontSize = 12.sp

            )
            Spacer(Modifier.height(18.dp))

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(

                    containerColor = Color(0xFF172033)

                ),

                shape = RoundedCornerShape(22.dp)

            ){

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalAlignment = Alignment.CenterVertically

                ){

                    Box(

                        modifier = Modifier
                            .size(55.dp)
                            .background(
                                Color(0xFF1D4ED8),
                                CircleShape
                            ),

                        contentAlignment = Alignment.Center

                    ){

                        Icon(

                            Icons.Default.AccountBalanceWallet,

                            null,

                            tint = Color.White

                        )

                    }

                    Spacer(Modifier.width(16.dp))

                    Column(

                        modifier = Modifier.weight(1f)

                    ){

                        Text(

                            "Available Balance",

                            color = Color.Gray

                        )

                        Text(

                            "₹ %.2f".format(wallet),

                            fontSize = 26.sp,

                            color = Color(0xFF22C55E),

                            fontWeight = FontWeight.Bold

                        )

                    }

                }

            }

            if(error.isNotEmpty()){

                Spacer(Modifier.height(15.dp))

                Text(

                    error,

                    color=Color.Red

                )

            }

            Spacer(Modifier.height(20.dp))

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = Color(0xFF2563EB)

                ),

                onClick = {

                    val bet = amount.toDoubleOrNull()

                    when {

                        number.isBlank() -> {

                            error = "Enter Number"

                        }

                        !BetValidator.validate(
                            gameCode,
                            number
                        ) -> {

                            error = BetValidator.error(gameCode)

                        }

                        bet == null -> {

                            error = "Enter Amount"

                        }

                        bet <= 0 -> {

                            error = "Invalid Amount"

                        }

                        bet > wallet -> {

                            error = "Insufficient Wallet"

                        }

                        else -> {

                            error = ""

                            onAdd(
                                number,
                                bet
                            )

                            onDismiss()

                        }

                    }

                }

            ){

                Icon(
                    Icons.Default.CurrencyRupee,
                    null
                )

                Spacer(Modifier.width(8.dp))

                Text(

                    "ADD BET",

                    fontWeight = FontWeight.Bold,

                    fontSize = 16.sp

                )

            }

            Spacer(Modifier.height(25.dp))

        }

    }

}