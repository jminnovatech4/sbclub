package com.jminnovatech.sbclub.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.*
import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.data.api.LoginResponse

class AuthVM : ViewModel() {

    var state by mutableStateOf<ApiState<LoginResponse>?>(null)

    fun login(context: Context, nav: NavController, phone:String, pass:String){

        val repo = AppRepository(context)
        val session = SessionManager(context)

        viewModelScope.launch {

            state = ApiState.Loading

            when(val result = repo.login(phone, pass)){

                is ApiState.Success -> {

                    val data = result.data

                    session.saveUser(
                        token = data.token,
                        role = data.role,
                        userId = data.user_code.toString(),
                        name = data.user.name,
                        balance = data.balance.toString()
                    )

                    when(data.role){
                        "admin" -> nav.navigate("admin")
                        "master" -> nav.navigate("master")
                        else -> nav.navigate("dashboard")
                    }
                }

                is ApiState.Error -> {
                    state = result
                }

                else -> {}
            }
        }
    }

    class ProfileVM(private val repo: AppRepository) : ViewModel() {

        var state by mutableStateOf<ApiState<String>?>(null)

        fun changePassword(old:String, new:String) {
            viewModelScope.launch {
                state = ApiState.Loading
                state = repo.changePassword(old, new)
            }
        }
    }
    var message by mutableStateOf("")

    fun loadMessage(context: Context) {
        viewModelScope.launch {
            try {
                val res = AppRepository(context).getAppMessage()
                message = res.message
            } catch (e: Exception) {
                message = ""
            }
        }
    }
}