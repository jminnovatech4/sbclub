package com.jminnovatech.sbclub.ui.screens.admin.game


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.AdminGameItem
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleItem
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM
@Composable
fun AdminScheduleModal(

    vm: AdminVM,

    game: AdminGameItem,

    onClose: () -> Unit

) {

    val context = LocalContext.current

    var selected by remember {
        mutableStateOf<AdminScheduleItem?>(null)
    }

    var showEdit by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        vm.loadSchedules(
            context,
            game.id
        )

    }

    AlertDialog(

        onDismissRequest = onClose,

        confirmButton = {},

        dismissButton = {},

        title = {

            Text("${game.game_name} Schedule")

        },

        text = {

            when(val state = vm.scheduleState){

                ApiState.Loading -> {

                    CircularProgressIndicator()

                }

                is ApiState.Error -> {

                    Text(state.message)

                }

                is ApiState.Success -> {

                    LazyColumn {

                        items(state.data.data){ item ->

                            ScheduleCard(

                                item = item,

                                onEdit = {

                                    selected = item

                                    showEdit = true

                                }

                            )

                        }

                    }

                }

                else -> {}

            }

        }

    )

    if(

        showEdit &&

        selected != null

    ){

        UpdateScheduleDialog(
            vm = vm,
            schedule = selected!!,
            onClose = {
                vm.resetGameActionState()
                showEdit = false

                vm.loadSchedules(
                    context,
                    game.id
                )

            }
        )

    }

}

@Composable
fun ScheduleCard(

    item: AdminScheduleItem,

    onEdit:()->Unit

){

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ){

        Column(

            Modifier.padding(12.dp)

        ){

            Text(item.title)

            Spacer(Modifier.height(6.dp))

            Text("Start : ${item.start_time}")

            Text("End : ${item.end_time}")

            Text("Result : ${item.result_time}")

            Spacer(Modifier.height(10.dp))

            Button(

                onClick = onEdit

            ){

                Icon(Icons.Default.Edit,null)

                Spacer(Modifier.width(6.dp))

                Text("Edit")

            }

        }

    }

}