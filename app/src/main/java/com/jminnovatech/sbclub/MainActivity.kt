package com.jminnovatech.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.jminnovatech.sbclub.navigation.AppNav
import com.jminnovatech.sbclub.utils.NetworkMonitor

class MainActivity : ComponentActivity() {

    private val openDeposit = mutableStateOf(false)
    private val sharedAmount = mutableStateOf("")
    private val sharedUtr = mutableStateOf("")
    private val receiptText = mutableStateOf("")
    private val upiApp = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        NetworkMonitor.start(this)

        readIntent(intent)

        setContent {

            AppNav(

                context = this,

                openDeposit = openDeposit.value,

                sharedAmount = sharedAmount.value,

                sharedUtr = sharedUtr.value,

                receiptText = receiptText.value,

                upiApp = upiApp.value

            )

        }

    }

    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)

        setIntent(intent)

        readIntent(intent)

    }

    private fun readIntent(intent: Intent?) {

        openDeposit.value =
            intent?.getBooleanExtra("open_deposit", false) ?: false

        sharedAmount.value =
            intent?.getStringExtra("amount") ?: ""

        sharedUtr.value =
            intent?.getStringExtra("utr") ?: ""

        receiptText.value =
            intent?.getStringExtra("receipt_text") ?: ""

        upiApp.value =
            intent?.getStringExtra("upi_app") ?: ""

    }

}