package com.jminnovatech.sbclub.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.viewmodel.AdminVM
import com.jminnovatech.sbclub.utils.ApiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultModal(onClose: () -> Unit) {

    val vm: AdminVM = viewModel()
    val context = LocalContext.current

    // 🔥 load rounds when modal opens
    LaunchedEffect(Unit) {
        vm.loadRounds(context)
    }

    ModalBottomSheet(
        onDismissRequest = { onClose() }
    ) {

        Column(Modifier.padding(16.dp)) {

            Text("🎯 Select Round",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(10.dp))

            when(val state = vm.roundsState){

                is ApiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ApiState.Success -> {

                    // 🔥 latest top
                    val list = state.data.sortedByDescending { it.id }

                    LazyColumn {

                        items(list.size){

                            val round = list[it]

                            val bgColor =
                                if(round.status == "running") Color(0xFFFFF3E0)
                                else Color(0xFFE8F5E9)

                            Card(
                                Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .clickable {

                                        vm.selectedRound = round
                                        vm.loadProfit(context, round.id)

                                        onClose() // close modal
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = bgColor
                                )
                            ) {

                                Column(Modifier.padding(16.dp)) {

                                    Text("Round ID: ${round.id}")

                                    Text("Start: ${round.start_time}")

                                    Text("Result Time: ${round.result_time}")

                                    if(round.status == "completed"){
                                        Text(
                                            "Result: ${round.result_number}",
                                            color = Color.Green
                                        )
                                    } else {
                                        Text(
                                            "🟡 Running...",
                                            color = Color(0xFFF57C00)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    Text(state.message, color = Color.Red)
                }

                else -> {}
            }
        }
    }
}