package com.jminnovatech.sbclub.ui.screens.admin.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleItem
import com.jminnovatech.sbclub.data.model.admin.game.UpdateScheduleRequest
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM
@Composable
fun UpdateScheduleDialog(

    vm: AdminVM,

    schedule: AdminScheduleItem,

    onClose:()->Unit

){

    val context = LocalContext.current

    var title by remember {
        mutableStateOf(schedule.title)
    }

    var start by remember {
        mutableStateOf(schedule.start_time)
    }

    var end by remember {
        mutableStateOf(schedule.end_time)
    }

    var result by remember {
        mutableStateOf(schedule.result_time)
    }

    var status by remember {
        mutableStateOf(schedule.status)
    }

    var active by remember {
        mutableStateOf(schedule.is_active == 1)
    }

    val action = vm.gameActionState
    LaunchedEffect(action) {

        when (action) {

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    action.data,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetGameActionState()

                onClose()
            }

            is ApiState.Error -> {

                Toast.makeText(
                    context,
                    action.message,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetGameActionState()
            }

            else -> {}
        }
    }
    AlertDialog(

        onDismissRequest = onClose,

        title = {

            Text("Update Schedule")

        },

        text = {

            Column(

                Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )

            ){

                OutlinedTextField(

                    value = title,

                    onValueChange = {

                        title = it

                    },

                    label = {

                        Text("Title")

                    }

                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(

                    value = start,

                    onValueChange = {

                        start = it

                    },

                    label = {

                        Text("Start Time")

                    }

                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(

                    value = end,

                    onValueChange = {

                        end = it

                    },

                    label = {

                        Text("End Time")

                    }

                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(

                    value = result,

                    onValueChange = {

                        result = it

                    },

                    label = {

                        Text("Result Time")

                    }

                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(

                    value = status,

                    onValueChange = {

                        status = it

                    },

                    label = {

                        Text("Status")

                    }

                )

                Spacer(Modifier.height(10.dp))

                Row{

                    Checkbox(

                        checked = active,

                        onCheckedChange = {

                            active = it

                        }

                    )

                    Text("Active")

                }

                Spacer(Modifier.height(10.dp))

                if(action is ApiState.Loading){

                    LinearProgressIndicator(

                        modifier = Modifier.fillMaxWidth()

                    )

                }

            }

        },
        confirmButton = {

            Button(

                onClick = {

                    vm.updateSchedule(

                        context,

                        UpdateScheduleRequest(

                            schedule_id = schedule.id,

                            title = title,

                            start_time = start,

                            end_time = end,

                            result_time = result,

                            status = status,

                            is_active = active

                        )

                    )

                }

            ){

                Text("SAVE")

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