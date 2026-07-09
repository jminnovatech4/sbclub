package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.progame.ProBetItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.jminnovatech.sbclub.viewmodel.ProGameVM
import com.jminnovatech.sbclub.utils.ApiState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(

    nav: NavController,
    context: Context,
    gameId: Int,
    scheduleId: Int,
    vm: ProGameVM = remember { ProGameVM() },
) {
    LaunchedEffect(gameId) {

        vm.loadSchedules(context, gameId)

        vm.loadCurrent(context, gameId)

        vm.loadWallet(context)

        vm.loadResults(context, gameId)
        vm.loadBetHistory(

            context,

            gameId,

            scheduleId

        )

    }
    LaunchedEffect(vm.scheduleState) {

        android.util.Log.d(
            "PLAY_GAME",
            vm.scheduleState.toString()
        )

    }
    val scheduleState = vm.scheduleState

    val schedule = (scheduleState as? ApiState.Success)
        ?.data
        ?.cards
        ?.firstOrNull {
            it.id == scheduleId
        }

    val game = (scheduleState as? ApiState.Success)
        ?.data
        ?.game
    LaunchedEffect(schedule) {

        android.util.Log.d(
            "PLAY_GAME",
            "Schedule = $schedule"
        )

    }

    LaunchedEffect(vm.betHistoryState){

        android.util.Log.d(

            "PLAY_BET_HISTORY",

            vm.betHistoryState.toString()

        )

    }
    vm.loadRunningBets(

        context,

        gameId,

        scheduleId

    )

    LaunchedEffect(vm.runningBetState){

        android.util.Log.d(

            "RUNNING_BET",

            vm.runningBetState.toString()

        )

    }
    val betList = remember {

        mutableStateListOf<ProBetItem>()

    }
    LaunchedEffect(vm.betState) {

        when(val state = vm.betState){

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    state.data,
                    Toast.LENGTH_SHORT
                ).show()

                vm.loadWallet(context)

                vm.loadCurrent(context, gameId)
                vm.loadBetHistory(

                    context,

                    gameId,

                    scheduleId

                )
                betList.clear()

                vm.clearBetState()

            }

            is ApiState.Error -> {

                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()

                vm.clearBetState()

            }

            else -> {}

        }

    }

    val wallet = vm.walletBalance

    val gameCode = game?.game_code ?: ""



    if(schedule==null){

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){

            CircularProgressIndicator()

        }

        return

    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = { Text(game?.game_name ?: "Play Game") },

                navigationIcon = {

                    IconButton(
                        onClick = { nav.popBackStack() }
                    ) {

                        Icon(Icons.Default.ArrowBack, null)

                    }

                }

            )

        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {

            item {

                WalletSummary(

                    wallet = wallet,

                    bet = betList.sumOf {

                        it.amount

                    }

                )

            }

            item {

                BetPanel(

                    gameCode = gameCode,

                    wallet = wallet,

                    betList = betList,

                    onPlaceBet = {

                        vm.placeBet(

                            context = context,

                            gameId = gameId,

                            scheduleId = schedule.id,

                            bets = betList.toList()

                        )

                    }

                )

            }

            val history =

                (vm.betHistoryState as? ApiState.Success)

                    ?.data

                    ?.data

                    ?: emptyList()

            if(history.isNotEmpty()){

                item{

                    Text(

                        "Your Running Bets",

                        style = MaterialTheme.typography.titleMedium

                    )

                }

                items(history){ bet ->

                    RunningBetCard(

                        bet = bet

                    )

                }

            }

        }

    }

}