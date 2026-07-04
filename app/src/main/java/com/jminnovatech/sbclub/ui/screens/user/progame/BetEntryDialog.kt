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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetEntryDialog(

    show:Boolean,

    digitLength:Int,

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

        containerColor = Color(0xFF0F172A)

    ){

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)

        ){

            Row(

                modifier=Modifier.fillMaxWidth()

            ){

                Text(

                    "Add Bet",

                    style=MaterialTheme.typography.headlineSmall,

                    color=Color.White,

                    fontWeight=FontWeight.Bold,

                    modifier=Modifier.weight(1f)

                )

                IconButton(

                    onClick=onDismiss

                ){

                    Icon(

                        Icons.Default.Close,

                        null,

                        tint=Color.White

                    )

                }

            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(

                value=number,

                onValueChange={

                    if(it.length<=digitLength){

                        number=it

                    }

                },

                modifier=Modifier.fillMaxWidth(),

                singleLine=true,

                label={

                    Text("Number")

                },

                keyboardOptions= KeyboardOptions(

                    keyboardType=KeyboardType.Number

                ),

                colors=OutlinedTextFieldDefaults.colors(

                    focusedContainerColor=Color(0xFF111827),

                    unfocusedContainerColor=Color(0xFF111827),

                    focusedTextColor=Color.White,

                    unfocusedTextColor=Color.White,

                    focusedBorderColor=Color(0xFF2563EB),

                    unfocusedBorderColor=Color.Gray

                )

            )

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(

                value=amount,

                onValueChange={

                    amount=it

                },

                modifier=Modifier.fillMaxWidth(),

                singleLine=true,

                label={

                    Text("Amount")

                },

                keyboardOptions=KeyboardOptions(

                    keyboardType=KeyboardType.Number

                ),

                colors=OutlinedTextFieldDefaults.colors(

                    focusedContainerColor=Color(0xFF111827),

                    unfocusedContainerColor=Color(0xFF111827),

                    focusedTextColor=Color.White,

                    unfocusedTextColor=Color.White,

                    focusedBorderColor=Color(0xFF2563EB),

                    unfocusedBorderColor=Color.Gray

                )

            )

            Spacer(Modifier.height(18.dp))

            Card(

                colors=CardDefaults.cardColors(

                    containerColor=Color(0xFF111827)

                )

            ){

                Column(

                    modifier=Modifier.padding(15.dp)

                ){

                    Text(

                        "Wallet",

                        color=Color.Gray

                    )

                    Text(

                        "₹ %.2f".format(wallet),

                        color=Color(0xFF22C55E),

                        fontWeight=FontWeight.Bold

                    )

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

                modifier=Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                onClick={

                    val bet=amount.toDoubleOrNull()

                    when{

                        number.length!=digitLength->{

                            error="Enter $digitLength digit number"

                        }

                        bet==null || bet<=0->{

                            error="Invalid Amount"

                        }

                        bet>wallet->{

                            error="Insufficient Wallet Balance"

                        }

                        else->{

                            error=""

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

                    Icons.Default.Add,

                    null

                )

                Spacer(Modifier.width(8.dp))

                Text("ADD BET")

            }

            Spacer(Modifier.height(25.dp))

        }

    }

}