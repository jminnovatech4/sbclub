package com.jminnovatech.sbclub.ui.screens.admin.game

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.AdminGameItem
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleItem
import com.jminnovatech.sbclub.data.model.admin.game.PublishResultRequest
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

@Composable
fun PublishResultDialog(

    vm: AdminVM,

    game: AdminGameItem,

    onClose:()->Unit

){

    val context = LocalContext.current

    var selected by remember {

        mutableStateOf<AdminScheduleItem?>(null)

    }

    var number by remember {

        mutableStateOf(
            selected?.result_number ?: ""
        )

    }

    LaunchedEffect(Unit){

        vm.loadSchedules(
            context,
            game.id
        )

    }

    val action = vm.gameActionState

    LaunchedEffect(action){

        when(action){

            is ApiState.Success->{

                Toast.makeText(
                    context,
                    action.data,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetGameActionState()

                onClose()

            }

            is ApiState.Error->{

                Toast.makeText(
                    context,
                    action.message,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetGameActionState()

            }

            else->{}

        }

    }
    LaunchedEffect(selected) {

        number = selected?.result_number ?: ""

    }
    AlertDialog(

        onDismissRequest = onClose,

        title = {

            Text("Publish Result")

        },

        text = {

            Column {

                when(val state = vm.scheduleState){

                    ApiState.Loading->{

                        CircularProgressIndicator()

                    }

                    is ApiState.Error->{

                        Text(state.message)

                    }

                    is ApiState.Success->{

                        LazyColumn(
                            modifier = Modifier.height(180.dp)
                        ){
                            items(state.data.data) { item ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {

                                    RadioButton(

                                        selected = selected?.id == item.id,

                                        onClick = {

                                            selected = item

                                        }

                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Column {

                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = "Start : ${item.start_time}"
                                        )

                                        Text(
                                            text = "End : ${item.end_time}"
                                        )

                                        if (!item.result_number.isNullOrEmpty()) {

                                            Text(
                                                text = "✅ Result : ${item.result_number}",
                                                color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
                                            )

                                        } else {

                                            Text(
                                                text = "❌ Result Pending",
                                                color = androidx.compose.ui.graphics.Color.Red
                                            )

                                        }

                                    }

                                }

                            }

                        }

                    }

                    else->{}

                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(

                    value = number,

                    onValueChange = {

                        number = it

                    },

                    label = {

                        Text("Result Number")

                    }

                )

                Spacer(Modifier.height(10.dp))

                if(action is ApiState.Loading){

                    LinearProgressIndicator(
                        modifier=Modifier.fillMaxWidth()
                    )

                }

            }

        },

        confirmButton = {

            Button(

                enabled =

                    selected!=null &&

                            number.isNotBlank(),

                onClick = {
                    val valid = when(game.digit_length){

                        1 -> number.length==1

                        2 -> number.length==2

                        3 -> number.length==3

                        5 -> number.length==5

                        else -> false

                    }

                    if(!valid){

                        Toast.makeText(

                            context,

                            "Invalid Result Number",

                            Toast.LENGTH_SHORT

                        ).show()

                        return@Button

                    }
                    vm.publishResult(

                        context,

                        PublishResultRequest(

                            game.id,

                            selected!!.id,

                            number

                        )

                    )

                }

            ){

                Text(

                    if (selected?.result_number.isNullOrEmpty())
                        "Publish"
                    else
                        "Update Result"

                )

            }

        },

        dismissButton = {

            OutlinedButton(

                onClick = onClose

            ){

                Text("Cancel")

            }

        }

    )

}