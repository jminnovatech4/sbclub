package com.sbclub.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.sbclub.sbclub.navigation.AppNav
import com.sbclub.sbclub.utils.NetworkMonitor
import com.sbclub.sbclub.utils.SessionManager

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

        // =========================================
        // DEPOSIT / UPI SHARE
        // =========================================

        val openDeposit =
            intent?.getBooleanExtra(
                "open_deposit",
                false
            ) ?: false

        val amount =
            intent?.getStringExtra("amount") ?: ""

        val utr =
            intent?.getStringExtra("utr") ?: ""

        val receipt =
            intent?.getStringExtra("receipt_text") ?: ""

        val upiApp =
            intent?.getStringExtra("upi_app") ?: ""


        // =========================================
        // FCM RESULT NOTIFICATION
        // =========================================

        val openResult =
            intent?.getBooleanExtra(
                "open_result",
                false
            ) ?: false

        val resultId =
            intent?.getStringExtra("result_id")

        val resultGroupId =
            intent?.getStringExtra("group_id")

        val resultBajiNo =
            intent?.getStringExtra("baji_no")


        // =========================================
        // SESSION
        // =========================================

        val session = SessionManager(this)


        // =========================================
        // APP NAVIGATION
        // =========================================

        setContent {

            AppNav(

                context = this,

                startDestination = "dashboard",

                openDeposit = openDeposit,

                sharedAmount = amount,

                sharedUtr = utr,

                receiptText = receipt,

                upiApp = upiApp,

                openResult = openResult,

                resultId = resultId,

                resultGroupId = resultGroupId,

                resultBajiNo = resultBajiNo

            )
        }
    }
}