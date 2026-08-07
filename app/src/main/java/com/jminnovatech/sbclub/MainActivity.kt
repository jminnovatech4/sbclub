package com.jminnovatech.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jminnovatech.sbclub.navigation.AppNav
import com.jminnovatech.sbclub.utils.NetworkMonitor
import com.jminnovatech.sbclub.utils.SessionManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        NetworkMonitor.start(this)

        openScreen(intent)

    }

    override fun onNewIntent(intent: Intent) {

        super.onNewIntent(intent)

        setIntent(intent)

        openScreen(intent)

    }

    private fun openScreen(intent: Intent?) {

        val openDeposit =
            intent?.getBooleanExtra("open_deposit", false) ?: false

        val amount =
            intent?.getStringExtra("amount") ?: ""

        val utr =
            intent?.getStringExtra("utr") ?: ""

        val receipt =
            intent?.getStringExtra("receipt_text") ?: ""

        val upiApp =
            intent?.getStringExtra("upi_app") ?: ""

        val session = SessionManager(this)

        setContent {

            if (openDeposit) {

                AppNav(

                    context = this,

                    startDestination = "dashboard",

                    openDeposit = true,

                    sharedAmount = amount,

                    sharedUtr = utr,

                    receiptText = receipt,

                    upiApp = upiApp

                )

            } else {

                AppNav(

                    context = this

                )

            }

        }

    }

}