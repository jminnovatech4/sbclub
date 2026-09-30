package com.sbclub.sbclub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.sbclub.sbclub.navigation.AppNav
import com.sbclub.sbclub.utils.NetworkMonitor
import com.sbclub.sbclub.utils.SessionManager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        NetworkMonitor.start(this)

        // Android 13+ notification permission
        requestNotificationPermission()

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
        // FCM ADMIN MESSAGE NOTIFICATION
        // =========================================

        val openMessage =
            intent?.getBooleanExtra(
                "open_message",
                false
            ) ?: false

        val messageId =
            intent?.getStringExtra(
                "message_id"
            )


        // =========================================
        // SESSION
        // =========================================

        val session = SessionManager(this)

        val role = session.getRole()


        // =========================================
        // START DESTINATION
        // =========================================
        //
        // Logged out  -> Login
        // User        -> Dashboard
        // Master      -> Master
        // Admin       -> Admin
        //
        // =========================================

        val startDestination = when (role) {

            "admin" -> "admin"

            "master" -> "master"

            "user" -> "dashboard"

            else -> "login"
        }


        // =========================================
        // APP NAVIGATION
        // =========================================

        setContent {

            AppNav(

                context = this,

                startDestination = startDestination,

                // =========================================
                // DEPOSIT / UPI
                // =========================================

                openDeposit = openDeposit,

                sharedAmount = amount,

                sharedUtr = utr,

                receiptText = receipt,

                upiApp = upiApp,

                // =========================================
                // FCM RESULT
                // =========================================

                openResult = openResult,

                resultId = resultId,

                resultGroupId = resultGroupId,

                resultBajiNo = resultBajiNo,

                // =========================================
                // FCM MESSAGE
                // =========================================

                openMessage = openMessage,

                messageId = messageId
            )
        }
    }


    // =========================================
    // ANDROID 13+ NOTIFICATION PERMISSION
    // =========================================

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    1001
                )
            }
        }
    }
}