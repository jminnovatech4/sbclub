package com.jminnovatech.sbclub.ui.screens.admin.game

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.AdminRateItem
import com.jminnovatech.sbclub.data.model.admin.game.UpdateRateRequest
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

@Composable
fun UpdateRateDialog(

    vm: AdminVM,

    rate: AdminRateItem,

    onClose: () -> Unit

) {

    val context = LocalContext.current

    var multiplier by remember {
        mutableStateOf(rate.win_multiplier.toString())
    }

    var active by remember {
        mutableStateOf(rate.is_active == 1)
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

            Text("Update Rate")

        },

        text = {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState()
                    )

            ) {

                OutlinedTextField(

                    value = multiplier,

                    onValueChange = {

                        multiplier = it

                    },

                    label = {

                        Text("Win Multiplier")

                    },

                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(
                    Modifier.height(12.dp)
                )

                Row {

                    Checkbox(

                        checked = active,

                        onCheckedChange = {

                            active = it

                        }

                    )

                    Spacer(
                        Modifier.width(6.dp)
                    )

                    Text("Active")

                }

                Spacer(
                    Modifier.height(12.dp)
                )

                if (action is ApiState.Loading) {

                    LinearProgressIndicator(

                        modifier = Modifier.fillMaxWidth()

                    )

                }

            }

        },

        confirmButton = {

            Button(

                onClick = {

                    vm.updateRate(

                        context,

                        UpdateRateRequest(

                            rate_id = rate.id,

                            win_multiplier = multiplier.toDoubleOrNull() ?: 0.0,

                            is_active = active

                        )

                    )

                }

            ) {

                Text("SAVE")

            }

        },

        dismissButton = {

            OutlinedButton(

                onClick = onClose

            ) {

                Text("Cancel")

            }

        }

    )

}