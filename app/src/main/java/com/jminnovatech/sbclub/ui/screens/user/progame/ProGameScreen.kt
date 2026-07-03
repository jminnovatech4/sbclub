package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.progame.Schedule
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM

@Composable
fun ProGameScreen(
    nav: NavController,
    context: Context,
    gameId: Int,
    vm: ProGameVM = remember { ProGameVM() }
) {

    LaunchedEffect(gameId) {
        vm.loadSchedules(context, gameId)
        vm.loadCurrent(context, gameId)
    }

    Scaffold(

        containerColor = Color(0xFF0F172A)

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (val state = vm.scheduleState) {

                ApiState.Idle -> {}

                ApiState.Loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }

                is ApiState.Error -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = state.message,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )

                    }

                }

                is ApiState.Success -> {

                    val currentId =
                        (vm.currentState as? ApiState.Success)
                            ?.data
                            ?.data
                            ?.id ?: 0

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {

                        items(state.data.cards) { schedule ->

                            ScheduleCard(

                                gameId = gameId,

                                schedule = schedule,

                                currentId = currentId,

                                wallet = vm.walletBalance,

                                resultNumber = "---",

                                onPlaceBet = { scheduleId, bets ->

                                    vm.placeBet(

                                        context = context,

                                        gameId = gameId,

                                        scheduleId = scheduleId,

                                        bets = bets

                                    )

                                },

                                onHistoryClick = { scheduleId ->

                                    vm.loadHistory(

                                        context,

                                        gameId,

                                        scheduleId

                                    )

                                }

                            )
                        }

                    }

                }

            }

        }

    }

}