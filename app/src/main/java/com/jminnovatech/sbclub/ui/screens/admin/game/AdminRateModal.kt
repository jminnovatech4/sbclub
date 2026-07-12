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
import com.jminnovatech.sbclub.data.model.admin.game.AdminRateItem
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

@Composable
fun AdminRateModal(

    vm: AdminVM,

    game: AdminGameItem,

    onClose: () -> Unit

) {

    val context = LocalContext.current

    var selected by remember {
        mutableStateOf<AdminRateItem?>(null)
    }

    var showEdit by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        vm.loadRates(
            context,
            game.id
        )

    }

    AlertDialog(

        onDismissRequest = onClose,

        confirmButton = {},

        dismissButton = {},

        title = {

            Text("${game.game_name} Rate")

        },

        text = {

            when (val state = vm.rateState) {

                ApiState.Loading -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {

                        CircularProgressIndicator()

                    }

                }

                is ApiState.Error -> {

                    Text(state.message)

                }

                is ApiState.Success -> {

                    LazyColumn {

                        items(state.data.data) { item ->

                            RateCard(

                                item = item,

                                onEdit = {

                                    vm.resetGameActionState()

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

    if (

        showEdit &&

        selected != null

    ) {

        UpdateRateDialog(

            vm = vm,

            rate = selected!!,

            onClose = {

                showEdit = false

                vm.loadRates(
                    context,
                    game.id
                )

            }

        )

    }

}

@Composable
fun RateCard(

    item: AdminRateItem,

    onEdit: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ) {

        Column(

            modifier = Modifier.padding(12.dp)

        ) {

            Text(
                text = "Multiplier : ${item.win_multiplier}"
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = if (item.is_active == 1)
                    "Status : Active"
                else
                    "Status : Inactive"
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Button(

                onClick = onEdit

            ) {

                Icon(
                    Icons.Default.Edit,
                    null
                )

                Spacer(
                    Modifier.width(6.dp)
                )

                Text("Edit")

            }

        }

    }

}