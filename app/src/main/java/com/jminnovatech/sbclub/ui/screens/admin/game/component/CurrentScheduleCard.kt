package com.jminnovatech.sbclub.ui.screens.admin.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.PublishGameResult
import com.jminnovatech.sbclub.data.model.admin.game.ResultPanelResponse
import com.jminnovatech.sbclub.viewmodel.AdminVM
import androidx.compose.ui.platform.LocalContext
@Composable
fun CurrentScheduleCard(
    data: ResultPanelResponse,
    vm: AdminVM,
    inputMap: MutableMap<Int, String>
) {

    val schedule = data.current_schedule ?: return
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = schedule.title ?: "Current Schedule",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${schedule.start_time} - ${schedule.end_time}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            data.games.forEach { game ->

                GameInputCard(
                    vm = vm,
                    scheduleId = schedule.id,
                    game = game,
                    inputMap = inputMap
                )

                Spacer(Modifier.height(16.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val list = mutableListOf<PublishGameResult>()

                    data.games.forEach {

                        val value = inputMap[it.id]?.trim().orEmpty()

                        if (value.isNotEmpty()) {

                            list.add(

                                PublishGameResult(

                                    game_id = it.id,

                                    result_number = value

                                )

                            )

                        }
                    }

                    if (list.isNotEmpty()) {

                        vm.publishAll(

                            context = context,

                            scheduleId = schedule.id,

                            results = list

                        )
                    }

                }

            ) {

                Text("Publish All")

            }

        }

    }

}