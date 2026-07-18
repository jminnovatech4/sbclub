package com.jminnovatech.sbclub.ui.screens.admin.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.ResultGame
import com.jminnovatech.sbclub.data.model.admin.game.ResultPreviewResponse
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

@Composable
fun GameInputCard(
    vm: AdminVM,
    scheduleId: Int,
    game: ResultGame,
    inputMap: MutableMap<Int, String>
) {

    val context = LocalContext.current

    var value by remember {

        mutableStateOf(

            inputMap[game.id] ?: ""

        )

    }

    val previewState =

        vm.resultPreviewMap[game.id]

    OutlinedCard(

        modifier = Modifier.fillMaxWidth()

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text(

                text = game.game_name,

                style = MaterialTheme.typography.titleMedium

            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(

                modifier = Modifier.fillMaxWidth(),

                value = value,

                onValueChange = {

                    if (it.length <= game.digit_length) {

                        value = it

                        inputMap[game.id] = it

                        if (it.length == game.digit_length) {

                            vm.previewResult(
                                context,
                                game.id,
                                game.schedule_id,
                                it
                            )

                        }

                    }

                },

                keyboardOptions = KeyboardOptions(

                    keyboardType = KeyboardType.Number

                ),

                label = {

                    Text(

                        "Result"

                    )

                }

            )

            Spacer(

                Modifier.height(12.dp)

            )

            when (previewState) {

                ApiState.Loading -> {

                    LinearProgressIndicator(

                        modifier = Modifier.fillMaxWidth()

                    )

                }

                is ApiState.Success -> {

                    PreviewCard(

                        previewState.data

                    )

                }

                is ApiState.Error -> {

                    Text(

                        previewState.message,

                        color = MaterialTheme.colorScheme.error

                    )

                }

                else -> {}

            }

        }

    }

}