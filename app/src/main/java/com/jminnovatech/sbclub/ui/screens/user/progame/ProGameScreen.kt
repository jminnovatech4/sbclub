package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProGameScreen(
    nav: NavController,
    context: Context,
    gameId: Int,
    vm: ProGameVM = remember { ProGameVM() }
) {

    // Screen open হলে একবার API call হবে
    LaunchedEffect(gameId) {

        // Game-এর সব schedule আনবে
        vm.loadSchedules(context, gameId)

        // বর্তমানে কোন schedule চলছে
        vm.loadCurrent(context, gameId)

        // Wallet balance আনবে
        vm.loadWallet(context)

        // Result list আনবে
      //  vm.loadResults(context, gameId)
    }
    LaunchedEffect(vm.betHistoryState) {

        when(val state = vm.betHistoryState){

            is ApiState.Success -> {

                android.util.Log.d(
                    "BET_HISTORY_SCREEN",
                    state.data.data.toString()
                )

            }

            is ApiState.Error -> {

                android.util.Log.e(
                    "BET_HISTORY_SCREEN",
                    state.message
                )

            }

            else -> {}
        }

    }

    LaunchedEffect(vm.betState) {

        when (val state = vm.betState) {

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    state.data.ifBlank { "Bet Placed Successfully" },
                    Toast.LENGTH_SHORT
                ).show()

                vm.clearBetState()
            }

            is ApiState.Error -> {

                Toast.makeText(
                    context,
                    state.message.ifBlank { "Something went wrong" },
                    Toast.LENGTH_SHORT
                ).show()

                vm.clearBetState()
            }

            else -> {}
        }
    }

    when (val state = vm.scheduleState) {

        ApiState.Idle,
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
                    color = Color.Red
                )

            }

        }

        is ApiState.Success -> {

            // বর্তমানে running schedule id
            val currentId =
                (vm.currentState as? ApiState.Success)
                    ?.data?.data?.id ?: 0

            Scaffold(

                topBar = {

                    GameTopBar(

                        // Game Name
                        title = state.data.game.game_name,

                        // Wallet
                        wallet = vm.walletBalance,

                        onBack = {
                            nav.popBackStack()
                        }

                    )

                }

            ) { padding ->

                LazyColumn(

                    modifier = Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 15.dp,
                        bottom = 15.dp,
                        start = 15.dp,
                        end = 15.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(15.dp)

                ) {

                    items(state.data.cards) { schedule ->

                        ScheduleCard(
                            nav = nav,
                            gameId = gameId,
                            schedule = schedule,
                            gameCode = state.data.game.game_code,
                            currentId = currentId,
                            wallet = vm.walletBalance,
                            betState = vm.betState,

                            resultNumber = schedule.result?.result_number,

                            onPlaceBet = { scheduleId, bets ->

                                vm.placeBet(
                                    context,
                                    gameId,
                                    scheduleId,
                                    bets
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