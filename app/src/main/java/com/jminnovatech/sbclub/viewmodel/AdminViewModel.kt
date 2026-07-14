package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.data.api.*
import com.jminnovatech.sbclub.data.model.LedgerItem
import com.jminnovatech.sbclub.data.model.ProfitDay
import com.jminnovatech.sbclub.data.model.RoundItem
import com.jminnovatech.sbclub.data.model.RoundItemUI
import com.jminnovatech.sbclub.data.model.admin.MasterReportResponse
import com.jminnovatech.sbclub.data.model.admin.PendingResponse
import com.jminnovatech.sbclub.data.model.admin.Transaction
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleResponse



import com.jminnovatech.sbclub.data.model.admin.game.UpdateScheduleRequest
import com.jminnovatech.sbclub.data.model.admin.game.UpdateRateRequest
import com.jminnovatech.sbclub.data.model.admin.game.PublishResultRequest

import com.jminnovatech.sbclub.model.UserNew
import com.jminnovatech.sbclub.data.model.admin.game.*
class AdminVM : ViewModel() {

    // 🔐 CREATE MASTER


    // 📊 PROFIT REPORT
    var profitState by mutableStateOf<ApiState<ProfitResponse>?>(null)

    // 🔁 CREATE ROUND
    var roundState by mutableStateOf<ApiState<String>?>(null)

    var createMasterState by mutableStateOf<ApiState<String>?>(null)
    var walletAddState by mutableStateOf<ApiState<String>?>(null)
    fun createMaster(context: Context, name:String, phone:String, pass:String){
        val repo = AppRepository(context)

        viewModelScope.launch {
            createMasterState = ApiState.Loading
            createMasterState = repo.createMaster(name, phone, pass)
        }
    }

    fun walletAdd(context: Context, userId:Int, amount:Int){
        val repo = AppRepository(context)

        viewModelScope.launch {
            walletAddState = ApiState.Loading
            walletAddState = repo.walletAdd(userId, amount)
        }
    }



    fun loadProfit(context: Context, round:Int){
        val repo = AppRepository(context)

        viewModelScope.launch {
            profitState = ApiState.Loading
            profitState = repo.profit(round)
        }
    }





    var manualState by mutableStateOf<ApiState<String>?>(null)

    fun manualResult(context: Context, round:Int, number:String){
        val repo = AppRepository(context)

        viewModelScope.launch {
            manualState = ApiState.Loading
            manualState = repo.manualResult(round, number)
        }
    }


    var roundsState by mutableStateOf<ApiState<List<RoundItemUI>>>(ApiState.Loading)
    var selectedRound by mutableStateOf<RoundItemUI?>(null)


    var previewState by mutableStateOf<ApiState<CurrentPreviewResponse>?>(null)

    fun loadCurrentPreview(context: Context){
        val repo = AppRepository(context)

        viewModelScope.launch {
            previewState = ApiState.Loading
            previewState = repo.currentPreview()
        }
    }

    var currentRoundState by mutableStateOf<ApiState<RoundItem>>(ApiState.Idle)

    fun loadCurrentRound(context: Context) {
        viewModelScope.launch {
            currentRoundState = ApiState.Loading

            try {
                val res = RetrofitClient.getApi(context).currentRound()
                currentRoundState = ApiState.Success(res)
            } catch (e: Exception) {
                currentRoundState = ApiState.Error(e.message ?: "Error")
            }
        }
    }

    var userListState by mutableStateOf<ApiState<List<UserNew>>>(ApiState.Idle)

    fun loadUsers(context: Context, search:String="", type:String=""){
        viewModelScope.launch {
            userListState = ApiState.Loading
            userListState = AppRepository(context).getUsers(search,type)
        }
    }
    var transactionState by mutableStateOf<ApiState<List<Transaction>>>(ApiState.Idle)

    fun loadTransactions(context: Context){
        viewModelScope.launch {
            transactionState = ApiState.Loading
            transactionState = AppRepository(context).getTransactions()
        }
    }
    var ledgerState by mutableStateOf<ApiState<List<LedgerItem>>>(ApiState.Loading)

    fun loadLedger(context: Context, bool: Boolean) {
        viewModelScope.launch {
            ledgerState = ApiState.Loading
            ledgerState = AppRepository(context).getLedger()
        }
    }

    var pendingState by mutableStateOf<ApiState<PendingResponse>>(ApiState.Idle)
    var transferState by mutableStateOf<ApiState<String>?>(null)

    fun loadPending(context: Context){
        viewModelScope.launch {
            pendingState = ApiState.Loading
            pendingState = AppRepository(context).getPendingUsers()
        }
    }

    fun transferAll(context: Context){
        viewModelScope.launch {
            transferState = ApiState.Loading
            transferState = AppRepository(context).transferPending()
        }
    }


    fun resetWalletState() {
        walletAddState = null
    }
    var profitListState by mutableStateOf<ApiState<List<ProfitDay>>>(ApiState.Idle)

    var updateState by mutableStateOf<ApiState<String>?>(null)

    fun updateMessage(context: Context, msg: String) {
        viewModelScope.launch {
            updateState = ApiState.Loading
            updateState = AppRepository(context).updateMessage(msg)
        }
    }

    var masterReportState by mutableStateOf<ApiState<MasterReportResponse>>(
        ApiState.Idle
    )
// ==============================
// ADMIN GAME
// ==============================
var gameDashboardState by mutableStateOf<ApiState<AdminDashboardResponse>>(ApiState.Idle)

    var gameListState by mutableStateOf<ApiState<List<AdminGameItem>>>(ApiState.Idle)

    var scheduleState by mutableStateOf<ApiState<AdminScheduleResponse>>(ApiState.Idle)

    var rateState by mutableStateOf<ApiState<AdminRateResponse>>(ApiState.Idle)

    var gameActionState by mutableStateOf<ApiState<String>>(ApiState.Idle)
    fun loadGameDashboard(context: Context) {

        viewModelScope.launch {

            gameDashboardState = ApiState.Loading

            gameDashboardState =
                AppRepository(context).adminGameDashboard()
        }

    }

    fun loadGames(context: Context) {

        viewModelScope.launch {

            gameListState = ApiState.Loading

            gameListState =
                AppRepository(context).adminGames()
        }

    }

    fun loadSchedules(
        context: Context,
        gameId: Int
    ) {

        viewModelScope.launch {

            scheduleState = ApiState.Loading

            scheduleState =
                AppRepository(context).adminSchedules(gameId)
        }

    }

    fun loadRates(
        context: Context,
        gameId: Int
    ) {

        viewModelScope.launch {

            rateState = ApiState.Loading

            rateState =
                AppRepository(context).adminRates(gameId)
        }

    }

    fun updateSchedule(
        context: Context,
        body: UpdateScheduleRequest
    ) {

        viewModelScope.launch {

            gameActionState = ApiState.Loading

            gameActionState =
                AppRepository(context).updateSchedule(body)
        }

    }

    fun updateRate(
        context: Context,
        body: UpdateRateRequest
    ) {

        viewModelScope.launch {

            gameActionState = ApiState.Loading

            gameActionState =
                AppRepository(context).updateRate(body)
        }

    }

    fun publishResult(
        context: Context,
        body: PublishResultRequest
    ) {

        viewModelScope.launch {

            gameActionState = ApiState.Loading

            gameActionState =
                AppRepository(context).publishResult(body)
        }

    }
    fun resetGameActionState() {
        gameActionState = ApiState.Idle
    }

    var resultReportState by mutableStateOf<ApiState<ResultReportResponse>>(ApiState.Idle)
    fun loadResultReport(

        context: Context,

        gameId: Int,

        scheduleId: Int,

        number: String? = null

    ){

        viewModelScope.launch {

            resultReportState = ApiState.Loading

            resultReportState =

                AppRepository(context)

                    .resultReport(

                        gameId,

                        scheduleId,

                        number

                    )

        }

    }

    fun clearResultPreview() {

        resultReportState = ApiState.Idle

    }
}