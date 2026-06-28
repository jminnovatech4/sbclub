package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import com.jminnovatech.sbclub.data.model.ResultWithBetResponse

import com.jminnovatech.sbclub.data.model.RoundItem
import com.jminnovatech.sbclub.data.model.user.BetItem.BetItem
import com.jminnovatech.sbclub.data.model.user.BetItem.BetRequest
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch

class GameVM : ViewModel() {
    var betState by mutableStateOf<ApiState<String>?>(null)
    var currentRoundState by mutableStateOf<ApiState<RoundItem>?>(null)

    fun placeBet(
        context: Context,
        bets: List<BetItem>,
        gameType: String = "1no"
    ) {
        viewModelScope.launch {
            betState = ApiState.Loading

            betState = AppRepository(context).placeBet(
                BetRequest(
                    game_type = gameType,
                    bets = bets
                )
            )
        }
    }

    fun loadCurrentRound(context: Context) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            currentRoundState = ApiState.Loading

            currentRoundState = repo.getCurrentRound()
        }
    }

    fun clearBetState() {
        betState = null
    }
    // 🔥 ADD THIS STATE
    var currentBetsState by mutableStateOf<ApiState<List<BetItem>>?>(null)

    // 🔥 ADD THIS FUNCTION
    fun loadCurrentBets(context: Context) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            currentBetsState = ApiState.Loading

            currentBetsState = repo.getCurrentBets()
        }
    }

    var resultState by mutableStateOf<ApiState<ResultWithBetResponse>>(ApiState.Idle)

    fun loadResults(context: Context) {

        viewModelScope.launch {

            val repo = AppRepository(context)

            resultState = ApiState.Loading

            resultState = try {
                repo.getUserResults()   // 🔥 IMPORTANT CHANGE
            } catch (e: Exception) {
                ApiState.Error(e.message ?: "Error")
            }
        }
    }
}