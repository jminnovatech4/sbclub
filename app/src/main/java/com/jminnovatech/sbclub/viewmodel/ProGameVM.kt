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

    var walletBalance by mutableDoubleStateOf(0.0)
// ============================================================
// Wallet
// ============================================================

    fun loadWallet(
        context: Context
    ) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            when (val state = repo.getProfile()) {

                is ApiState.Success -> {

                    walletBalance =
                        state.data.wallet.toDoubleOrNull() ?: 0.0

                }

                else -> {

                }

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
    // Load Schedule
    // ============================================================

    fun loadSchedules(

        context: Context,

        gameId: Int

    ) {

        viewModelScope.launch {

            scheduleState = ApiState.Loading

            scheduleState =
                AppRepository(context)
                    .getProSchedules(gameId)

        }

    }

    // ============================================================
    // Current Schedule
    // ============================================================

    fun loadCurrent(

        context: Context,

        gameId: Int

    ) {

        viewModelScope.launch {

            currentState = ApiState.Loading

            currentState =
                AppRepository(context)
                    .getCurrentProSchedule(gameId)

        }

    }

    // ============================================================
    // Place Bet
    // ============================================================

    fun placeBet(

        context: Context,

        gameId: Int,

        scheduleId: Int,

        bets: List<ProBetItem>

    ) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            betState = ApiState.Loading

            betState = repo.placeProBet(

                ProBetRequest(

                    game_id = gameId,

                    schedule_id = scheduleId,

                    bets = bets

                )

            )

            if (betState is ApiState.Success) {

                loadWallet(context)

                loadCurrent(context, gameId)

                loadResults(context, gameId)

                loadHistory(context, gameId, scheduleId)
                loadBetHistory(
                    context,
                    gameId,
                    scheduleId
                )

            }

        }

    }

    // ============================================================
    // History
    // ============================================================

    fun loadHistory(

        context: Context,

        gameId: Int,

        scheduleId: Int? = null

    ) {

        viewModelScope.launch {

            historyState = ApiState.Loading

            historyState =
                AppRepository(context)
                    .getProHistory(

                        gameId,

                        scheduleId

                    )

        }

    }

    // ============================================================
    // Result
    // ============================================================

    fun loadResults(

        context: Context,

        gameId: Int

    ) {

        viewModelScope.launch {

            resultState = ApiState.Loading

            resultState =
                AppRepository(context)
                    .getProResults(gameId)

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
        scheduleId: Int
    ) {

        viewModelScope.launch {

            betHistoryState = ApiState.Loading

            betHistoryState = AppRepository(context)
                .getBetHistory(gameId, scheduleId)

            android.util.Log.d(
                "BET_HISTORY_VM",
                betHistoryState.toString()
            )
        }
    }
}