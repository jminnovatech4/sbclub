package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jminnovatech.sbclub.data.model.LedgerItem
import com.jminnovatech.sbclub.data.model.master.MasterUser
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch

class MasterVM(context: Context) : ViewModel() {

    private val repo = AppRepository(context)

    var userState by mutableStateOf<ApiState<List<MasterUser>>>(ApiState.Loading)
    var actionState by mutableStateOf<ApiState<String>?>(null)

    var search by mutableStateOf("")

    var users by mutableStateOf<List<MasterUser>>(emptyList())
    var balance by mutableStateOf(0.0)
    fun loadUsers() {
        viewModelScope.launch {
            userState = ApiState.Loading

            val res = repo.getMyUsers(search)

            userState = res

            if (res is ApiState.Success) {
                users = res.data
                balance = users.sumOf { it.balance }
            }
        }
    }

    var successMsg by mutableStateOf<String?>(null)
    fun createUser(name:String, phone:String, pass:String){
        viewModelScope.launch {
            val res = repo.createUser(name, phone, pass)
            if(res is ApiState.Success){
                successMsg = "User Created Successfully"
            }
            loadUsers()
        }
    }

    fun transfer(code:String, amount:Int){
        viewModelScope.launch {
            val res = repo.transfer(code, amount)
            if(res is ApiState.Success){
                successMsg = "Transfer Successful"
            }
            loadUsers()
        }
    }


    var ledgerState by mutableStateOf<ApiState<List<LedgerItem>>>(ApiState.Loading)

    fun loadLedger() {
        viewModelScope.launch {
            ledgerState = repo.getLedger() // same function reuse
        }
    }


}