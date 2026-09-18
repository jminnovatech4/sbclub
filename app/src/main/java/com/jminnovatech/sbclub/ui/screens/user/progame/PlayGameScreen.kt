package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.progame.ProBetItem
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(

    nav: NavController,
    context: Context,

    groupId: Int,

    gameId: Int,

    scheduleId: Int,

    vm: ProGameVM = remember { ProGameVM() }

) {

    // ============================================================
    // LOAD DATA
    // ============================================================

    LaunchedEffect(groupId, gameId, scheduleId) {

        vm.loadSchedules(
            context,
            gameId,
            groupId
        )

        vm.loadCurrent(
            context,
            gameId,
            groupId
        )

        vm.loadResults(
            context,
            gameId,
            groupId
        )

        vm.loadBetHistory(
            context,
            gameId,
            scheduleId,
            groupId
        )

        vm.loadRunningBets(
            context,
            gameId,
            scheduleId,
            groupId
        )
    }


    // ============================================================
    // SCHEDULE LOG
    // ============================================================

    LaunchedEffect(vm.scheduleState) {

        android.util.Log.d(
            "PLAY_GAME",
            vm.scheduleState.toString()
        )
    }


    val scheduleState = vm.scheduleState


    // ============================================================
    // FIND CURRENT SCHEDULE
    // ============================================================

    val schedule =
        (scheduleState as? ApiState.Success)
            ?.data
            ?.cards
            ?.firstOrNull {
                it.id == scheduleId
            }


    // ============================================================
    // GAME
    // ============================================================

    val game =
        (scheduleState as? ApiState.Success)
            ?.data
            ?.game


    // ============================================================
    // CURRENT RUNNING SCHEDULE
    // ============================================================

    val currentId =
        (vm.currentState as? ApiState.Success)
            ?.data
            ?.data
            ?.id ?: 0


    val isRunning =
        schedule?.id == currentId


    // ============================================================
    // SCHEDULE LOG
    // ============================================================

    LaunchedEffect(schedule) {

        android.util.Log.d(
            "PLAY_GAME",
            "Schedule = $schedule"
        )
    }


    // ============================================================
    // BET HISTORY LOG
    // ============================================================

    LaunchedEffect(vm.betHistoryState) {

        android.util.Log.d(
            "PLAY_BET_HISTORY",
            vm.betHistoryState.toString()
        )
    }


    // ============================================================
    // RUNNING BETS
    // ============================================================

    LaunchedEffect(groupId, gameId, scheduleId) {

        vm.loadRunningBets(
            context,
            gameId,
            scheduleId,
            groupId
        )
    }


    LaunchedEffect(vm.runningBetState) {

        android.util.Log.d(
            "RUNNING_BET",
            vm.runningBetState.toString()
        )
    }


    // ============================================================
    // BET LIST
    // ============================================================

    val betList = remember {

        mutableStateListOf<ProBetItem>()

    }


    // ============================================================
    // BET STATE
    // ============================================================

    LaunchedEffect(vm.betState) {

        when (val state = vm.betState) {

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    state.data,
                    Toast.LENGTH_SHORT
                ).show()

                vm.loadWallet(context)

                vm.loadCurrent(
                    context,
                    gameId,
                    groupId
                )

                vm.loadBetHistory(
                    context,
                    gameId,
                    scheduleId,
                    groupId
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


    // ============================================================
    // WALLET
    // ============================================================

    val wallet =
        vm.walletBalance


    // ============================================================
    // GAME CODE
    // ============================================================

    val gameCode =
        game?.game_code ?: ""


    // ============================================================
    // WAITING FOR SCHEDULE
    // ============================================================

    if (schedule == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()

        }

        return
    }


    // ============================================================
    // SCREEN
    // ============================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        game?.game_name ?: "Play Game"
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { padding ->


        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)

        ) {


            // ====================================================
            // WALLET
            // ====================================================

            item {

                WalletSummary(

                    wallet = wallet,

                    bet = betList.sumOf {
                        it.amount
                    }
                )
            }


            // ====================================================
            // BET PANEL
            // ====================================================

            if (isRunning) {

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

                                groupId = groupId,

                                bets = betList.toList()
                            )
                        }
                    )
                }
            }


            // ====================================================
            // BET HISTORY
            // ====================================================

            val history =
                (vm.betHistoryState as? ApiState.Success)
                    ?.data
                    ?.data
                    ?: emptyList()


            if (history.isNotEmpty()) {

                item {

                    Text(

                        text =
                            if (isRunning)
                                "Your Running Bets"
                            else
                                "Your Bet History",

                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }


                items(history) { bet ->

                    RunningBetCard(

                        bet = bet,

                        isRunning = isRunning
                    )
                }
            }
        }
    }
}