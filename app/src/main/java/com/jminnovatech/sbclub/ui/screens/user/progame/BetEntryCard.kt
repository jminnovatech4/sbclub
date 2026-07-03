package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BetEntryCard(

    onAdd:(String,Double)->Unit

){

    var number by remember {

        mutableStateOf("")

    }

    var amount by remember {

        mutableStateOf("")

    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF1E293B)

        )

    ){

        Column(

            modifier = Modifier.padding(16.dp)

        ){

            Text(

                "Enter Number",

                color = Color.White

            )

            Spacer(

                Modifier.height(8.dp)

            )

            OutlinedTextField(

                value = number,

                onValueChange = {

                    number = it

                },

                singleLine = true,

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(

                    keyboardType = KeyboardType.Number

                )

            )

            Spacer(

                Modifier.height(14.dp)

            )

            Text(

                "Bet Amount",

                color = Color.White

            )

            Spacer(

                Modifier.height(8.dp)

            )

            OutlinedTextField(

                value = amount,

                onValueChange = {

                    amount = it

                },

                singleLine = true,

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(

                    keyboardType = KeyboardType.Number

                )

            )

            Spacer(

                Modifier.height(20.dp)

            )

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                onClick = {

                    val bet = amount.toDoubleOrNull()

                    if(

                        number.isNotEmpty()

                        &&

                        bet!=null

                    ){

                        onAdd(

                            number,

                            bet

                        )

                        number=""

                        amount=""

                    }

                }

            ){

                Icon(

                    Icons.Default.Add,

                    null

                )

                Spacer(

                    Modifier.width(8.dp)

                )

                Text(

                    "ADD BET"

                )

            }

        }

    }

}