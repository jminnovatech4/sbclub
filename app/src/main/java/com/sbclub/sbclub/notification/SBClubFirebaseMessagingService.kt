package com.sbclub.sbclub.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sbclub.sbclub.MainActivity
import com.sbclub.sbclub.R
import com.sbclub.sbclub.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SBClubFirebaseMessagingService :
    FirebaseMessagingService() {

    companion object {

        private const val TAG = "SBCLUB_FCM"

        private const val CHANNEL_ID =
            "sbclub_results"

        private const val CHANNEL_NAME =
            "SB CLUB Results"
    }


    // =====================================================
    // FCM TOKEN
    // =====================================================

    override fun onNewToken(token: String) {

        super.onNewToken(token)

        Log.d(
            TAG,
            "FCM Token = $token"
        )

        registerTokenToServer(token)
    }


    private fun registerTokenToServer(
        token: String
    ) {

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val repository =
                    AppRepository(applicationContext)

                val result =
                    repository.registerFcmToken(token)

                Log.d(
                    TAG,
                    "FCM token registration = $result"
                )

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "FCM token registration failed",
                    e
                )
            }
        }
    }


    // =====================================================
    // FCM MESSAGE
    // =====================================================

    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(
            remoteMessage
        )

        Log.d(
            TAG,
            "FCM message received"
        )


        val title =
            remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: "SB CLUB"


        val body =
            remoteMessage.notification?.body
                ?: remoteMessage.data["body"]
                ?: "New Notification"


        showNotification(

            title = title,

            body = body,

            data = remoteMessage.data
        )
    }


    // =====================================================
    // SHOW NOTIFICATION
    // =====================================================

    private fun showNotification(

        title: String,

        body: String,

        data: Map<String, String>

    ) {

        createNotificationChannel()


        val openResult =
            data["open_result"]
                ?.toBoolean()
                ?: false


        val openMessage =
            data["open_message"]
                ?.toBoolean()
                ?: false


        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP


                // -----------------------------
                // RESULT
                // -----------------------------

                putExtra(
                    "open_result",
                    openResult
                )

                putExtra(
                    "result_id",
                    data["result_id"]
                )

                putExtra(
                    "group_id",
                    data["group_id"]
                )

                putExtra(
                    "baji_no",
                    data["baji_no"]
                )


                // -----------------------------
                // ADMIN MESSAGE
                // -----------------------------

                putExtra(
                    "open_message",
                    openMessage
                )

                putExtra(
                    "message_id",
                    data["message_id"]
                )
            }


        val pendingIntent =
            PendingIntent.getActivity(

                this,

                System.currentTimeMillis()
                    .toInt(),

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )


        val builder =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification_sbclub)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .setContentIntent(
                    pendingIntent
                )


        if (
            Build.VERSION.SDK_INT < 33 ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(

                    System.currentTimeMillis()
                        .toInt(),

                    builder.build()
                )
        }
    }


    // =====================================================
    // NOTIFICATION CHANNEL
    // =====================================================

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(

                    CHANNEL_ID,

                    CHANNEL_NAME,

                    NotificationManager
                        .IMPORTANCE_HIGH

                ).apply {

                    description =
                        "SB CLUB result and announcement notifications"
                }


            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager


            manager.createNotificationChannel(
                channel
            )
        }
    }
}