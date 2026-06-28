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

import com.jminnovatech.sbclub.model.UserNew

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

    fun loadRounds(context: Context){
        val repo = AppRepository(context)

        viewModelScope.launch {
            roundsState = ApiState.Loading
            roundsState = repo.getRounds()
        }
    }
    var previewState by mutableStateOf<ApiState<CurrentPreviewResponse>?>(null)

    fun loadCurrentPreview(context: Context){
        val repo = AppRepository(context)

        viewModelScope.launch {
            previewState = ApiState.Loading
            previewState = repo.currentPreview()
        }
    }
    fun clearPreview(){
        previewState = null
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

    fun manualPattiResult(context: Context, number:String){
        val repo = AppRepository(context)

        viewModelScope.launch {
            manualState = ApiState.Loading
            manualState = repo.manualPattiResult(number)
        }
    }
    fun resetWalletState() {
        walletAddState = null
    }
    var profitListState by mutableStateOf<ApiState<List<ProfitDay>>>(ApiState.Idle)

    fun loadProfitList(context: Context, from:String?=null, to:String?=null){
        viewModelScope.launch {
            profitListState = ApiState.Loading
            profitListState = AppRepository(context).getProfitList(from, to)
        }
    }
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

    fun loadMasterReport(
        context: Context,
        from: String? = null,
        to: String? = null
    ) {

        viewModelScope.launch {

            masterReportState = ApiState.Loading

            try {

                // 🔥 ADD THIS
                Log.d(
                    "MASTER_REPORT_API",
                    "FROM = $from TO = $to"
                )

                val response =
                    AppRepository(context)
                        .masterTeamReport(from, to)

                // 🔥 ADD THIS
                Log.d(
                    "MASTER_REPORT_SUCCESS",
                    response.toString()
                )

                masterReportState =
                    ApiState.Success(response)

            } catch (e: Exception) {

                // 🔥 ADD THIS
                Log.d(
                    "MASTER_REPORT_ERROR",
                    e.toString()
                )

                ApiState.Error(e.message ?: "Error")
            }
        }
    }
}