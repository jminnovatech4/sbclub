package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jminnovatech.sbclub.data.model.progame.*
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch

class ProGameVM : ViewModel() {

    // -------------------------
    // Games
    // -------------------------

    var gamesState by mutableStateOf<ApiState<List<Game>>>(ApiState.Idle)
        private set

    // -------------------------
    // Schedule
    // -------------------------

    var scheduleState by mutableStateOf<ApiState<ScheduleResponse>>(ApiState.Idle)
        private set

    // -------------------------
    // Current Schedule
    // -------------------------

    var currentState by mutableStateOf<ApiState<CurrentScheduleResponse>>(ApiState.Idle)
        private set

    // -------------------------
    // Result
    // -------------------------

    var resultState by mutableStateOf<ApiState<ResultResponse>>(ApiState.Idle)
        private set

    // -------------------------
    // History
    // -------------------------

    var historyState by mutableStateOf<ApiState<HistoryResponse>>(ApiState.Idle)
        private set

    // -------------------------
    // Place Bet
    // -------------------------

    var betState by mutableStateOf<ApiState<String>>(ApiState.Idle)
        private set

    // -------------------------
    // Wallet
    // -------------------------

// ============================================================
// Wallet
// ============================================================

    var walletBalance by mutableDoubleStateOf(0.0)

    fun loadWallet(
        context: Context
    ) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            when (val state = repo.getProfile()) {

                is ApiState.Success -> {

                    walletBalance =
                        state.data.wallet
                            .toDoubleOrNull()
                            ?: 0.0

                    android.util.Log.d(
                        "PRO_WALLET",
                        "Wallet Balance = $walletBalance"
                    )
                }

                is ApiState.Error -> {

                    android.util.Log.e(
                        "PRO_WALLET",
                        "Wallet Error = ${state.message}"
                    )
                }

                else -> {}
            }
        }
    }
    // =======================================a=====================
    // Load Games
    // ============================================================

    fun loadGames(context: Context) {

        viewModelScope.launch {

            gamesState = ApiState.Loading

            gamesState =
                AppRepository(context).getProGames()

        }

    }
// ============================================================
// Group Games
// ============================================================

    var groupGamesState by mutableStateOf<ApiState<List<Game>>>(
        ApiState.Idle
    )
        private set

    fun loadGroupGames(
        context: Context,
        groupId: Int
    ) {

        viewModelScope.launch {

            groupGamesState = ApiState.Loading

            groupGamesState =
                AppRepository(context)
                    .getGroupGames(groupId)
        }
    }


// ============================================================
// Load Schedule
// ============================================================

    fun loadSchedules(
        context: Context,
        gameId: Int,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            scheduleState = ApiState.Loading

            scheduleState =
                AppRepository(context)
                    .getProSchedules(
                        gameId = gameId,
                        groupId = groupId
                    )
        }
    }

    // ============================================================
    // Current Schedule
    // ============================================================

// ============================================================
// Current Schedule
// ============================================================

// ============================================================
// Current Schedule
// ============================================================

    fun loadCurrent(
        context: Context,
        gameId: Int,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            currentState = ApiState.Loading

            currentState =
                AppRepository(context)
                    .getCurrentProSchedule(
                        gameId = gameId,
                        groupId = groupId
                    )
        }
    }

    // ============================================================
    // Place Bet
    // ============================================================

    fun placeBet(
        context: Context,
        gameId: Int,
        scheduleId: Int,
        groupId: Int,
        bets: List<ProBetItem>
    ) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            betState = ApiState.Loading

            betState = repo.placeProBet(

                ProBetRequest(

                    game_id = gameId,

                    schedule_id = scheduleId,

                    group_id = groupId,

                    bets = bets

                )

            )

            if (betState is ApiState.Success) {

                loadWallet(context)

                loadCurrent(
                    context,
                    gameId,
                    groupId
                )

                loadResults(
                    context,
                    gameId,
                    groupId
                )

                loadHistory(
                    context,
                    gameId,
                    scheduleId,
                    groupId
                )

                loadBetHistory(
                    context,
                    gameId,
                    scheduleId,
                    groupId
                )
            }
        }
    }

    // ============================================================
    // History
    // ============================================================

// ============================================================
// History
// ============================================================

    fun loadHistory(
        context: Context,
        gameId: Int,
        scheduleId: Int? = null,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            historyState = ApiState.Loading

            historyState =
                AppRepository(context)
                    .getProHistory(
                        gameId = gameId,
                        scheduleId = scheduleId,
                        groupId = groupId
                    )
        }
    }

    // ============================================================
    // Result
    // ============================================================

    fun loadResults(
        context: Context,
        gameId: Int,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            resultState = ApiState.Loading

            resultState =
                AppRepository(context)
                    .getProResults(
                        gameId = gameId,
                        groupId = groupId
                    )

        }
    }

    // ============================================================
    // Wallet
    // ============================================================

    fun updateWallet(balance: Double) {

        walletBalance = balance

    }

    fun clearBetState() {

        betState = ApiState.Idle

    }
    var betHistoryState by mutableStateOf<ApiState<BetHistoryResponse>>(ApiState.Idle)
        private set

    fun loadBetHistory(
        context: Context,
        gameId: Int,
        scheduleId: Int,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            betHistoryState = ApiState.Loading

            betHistoryState =
                AppRepository(context)
                    .getBetHistory(
                        gameId = gameId,
                        scheduleId = scheduleId,
                        groupId = groupId
                    )

            android.util.Log.d(
                "BET_HISTORY_VM",
                betHistoryState.toString()
            )
        }
    }
    var runningBetState by mutableStateOf<ApiState<RunningBetResponse>>(ApiState.Idle)
    fun loadRunningBets(
        context: Context,
        gameId: Int,
        scheduleId: Int,
        groupId: Int = 1
    ) {

        viewModelScope.launch {

            runningBetState = ApiState.Loading

            try {

                val repo = AppRepository(context)

                val res = repo.runningBets(
                    gameId = gameId,
                    scheduleId = scheduleId,
                    groupId = groupId
                )

                runningBetState =
                    ApiState.Success(res)

            } catch (e: Exception) {

                runningBetState =
                    ApiState.Error(
                        e.message ?: "Error"
                    )

            }

        }
    }
    // ============================================================
// Groups
// ============================================================

    var groupsState by mutableStateOf<ApiState<List<GameGroup>>>(
        ApiState.Idle
    )
        private set

    fun loadGroups(context: Context) {

        viewModelScope.launch {

            groupsState = ApiState.Loading

            groupsState =
                AppRepository(context)
                    .getGameGroups()
        }
    }



}