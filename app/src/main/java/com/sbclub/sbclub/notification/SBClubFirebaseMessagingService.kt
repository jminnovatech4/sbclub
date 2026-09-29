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

class SBClubFirebaseMessagingService :
    FirebaseMessagingService() {

    companion object {
        private const val TAG = "SBCLUB_FCM"

        private const val CHANNEL_ID = "sbclub_results"
        private const val CHANNEL_NAME = "SB CLUB Results"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "FCM Token = $token")

        // Laravel API-তে token পাঠাব
        // পরের ধাপে এখানে সেট করব
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FCM message received")

        val title =
            remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: "SB CLUB"

        val body =
            remoteMessage.notification?.body
                ?: remoteMessage.data["body"]
                ?: "New Result Available"

        showNotification(
            title = title,
            body = body,
            data = remoteMessage.data
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {

        createNotificationChannel()

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP

            putExtra(
                "open_result",
                true
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
        }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val builder =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    System.currentTimeMillis().toInt(),
                    builder.build()
                )
        }
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        "SB CLUB game result notifications"
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