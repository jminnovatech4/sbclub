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
import android.content.Intent
import android.net.Uri
import android.widget.Toast

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

    var upiName by mutableStateOf("")
        private set

    var upiId by mutableStateOf("")
        private set

    var qrImage by mutableStateOf("")
        private set
    fun loadMessage(context: Context) {

        viewModelScope.launch {

            try {

                val res = AppRepository(context).getAppMessage()

                message = res.message

                upiName = res.upi_name

                upiId = res.upi_id

                qrImage = res.qr_image

            } catch (e: Exception) {

                message = ""

                upiName = ""

                upiId = ""

                qrImage = ""

            }

        }

    }
    fun openUpi(
        context: Context,
        upiId: String,
        upiName: String,
        amount: String
    ) {

        try {

            val uri = Uri.parse(
                "upi://pay" +
                        "?pa=$upiId" +
                        "&pn=$upiName" +
                        "&am=$amount" +
                        "&cu=INR" +
                        "&tn=SB Club Deposit"
            )

            val intent = Intent(Intent.ACTION_VIEW, uri)

            context.startActivity(
                Intent.createChooser(
                    intent,
                    "Pay Using"
                )
            )

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                context,
                e.message ?: "No UPI App Found",
                Toast.LENGTH_LONG
            ).show()

        }

    }

    var depositState by mutableStateOf<ApiState<String>>(ApiState.Idle)
        private set

    fun submitDeposit(

        context: Context,

        amount: Double,

        transactionId: String

    ) {

        viewModelScope.launch {

            depositState = ApiState.Loading

            depositState =

                AppRepository(context)

                    .depositRequest(

                        amount,

                        transactionId

                    )

        }

    }
}