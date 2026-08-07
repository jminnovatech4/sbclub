package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jminnovatech.sbclub.data.model.LedgerItem
import com.jminnovatech.sbclub.data.model.Payment
import com.jminnovatech.sbclub.data.model.ProfileResponse
import com.jminnovatech.sbclub.data.model.ResultWithBetResponse
import com.jminnovatech.sbclub.data.model.SummaryResponse
import com.jminnovatech.sbclub.data.model.WalletRequest
import com.jminnovatech.sbclub.data.model.WithdrawItem
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch


class WalletVM(private val repo: AppRepository) : ViewModel() {

    var summaryState by mutableStateOf<ApiState<SummaryResponse>>(ApiState.Loading)
    var ledgerState by mutableStateOf<ApiState<List<LedgerItem>>>(ApiState.Loading)

    fun loadSummary() {
        viewModelScope.launch {
            summaryState = repo.getSummary()
        }
    }

    fun loadLedger() {
        viewModelScope.launch {
            ledgerState = repo.getLedger()
        }
    }
    var resultState by mutableStateOf<ApiState<ResultWithBetResponse>>(ApiState.Loading)

    fun loadResults(context: Context) {
        viewModelScope.launch {
            resultState = repo.getUserResults()
        }
    }
    var withdrawState by mutableStateOf<ApiState<String>?>(null)

    // 🔥 FIX 2: correct type
    var pendingState by mutableStateOf<ApiState<List<WithdrawItem>>>(ApiState.Loading)

    // 🔥 withdraw API call
    fun withdraw(
        amount: Double,
        type: String,
        acc: String,
        ifsc: String,
        holder: String
    ) {
        viewModelScope.launch {
            withdrawState = repo.withdrawWithPayment(
                amount,
                type,
                acc,
                holder,
                ifsc
            )
        }
    }

    // 🔥 pending API call
    fun loadPending() {
        viewModelScope.launch {
            pendingState = repo.getPendingWithdraws()
        }
    }

    // 🔥 approve
    fun approve(id: Int) {
        viewModelScope.launch {

            withdrawState = ApiState.Loading   // 🔥 ADD

            val res = repo.approveWithdraw(id)
            Log.d("APPROVE_RES", res.toString())
            withdrawState = res   // 🔥 RESULT STORE

            if (res is ApiState.Success) {
                loadPending()

                loadProfile()
            }
        }
    }

    // 🔥 reject
    fun reject(id: Int) {
        viewModelScope.launch {

            withdrawState = ApiState.Loading   // 🔥 add

            val res = repo.rejectWithdraw(id)
            Log.d("REJECT_RES", res.toString())
            withdrawState = res   // 🔥 result set

            if (res is ApiState.Success) {
                loadPending()
                loadProfile()   // 🔥 balance update
            }
        }
    }
    var historyState by mutableStateOf<ApiState<List<WithdrawItem>>>(ApiState.Loading)

    fun loadHistory(){
        viewModelScope.launch {
            historyState = repo.getWithdrawHistory()
        }
    }
    var paymentState by mutableStateOf<ApiState<Payment?>>(ApiState.Loading)


    var profileState by mutableStateOf<ApiState<ProfileResponse>>(ApiState.Loading)

    fun loadProfile(){
        viewModelScope.launch {
            profileState = repo.getProfile()
        }
    }

    // ======================================
// DEPOSIT HISTORY
// ======================================


}
