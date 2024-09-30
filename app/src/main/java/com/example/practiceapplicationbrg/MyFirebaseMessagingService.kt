package com.example.practiceapplicationbrg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Log the sender of the message
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body)
        }

        // Also display a toast with the message details
        sendToast(remoteMessage.from, remoteMessage.notification?.body)
    }

    private fun sendToast(from: String?, body: String?) {
        // Ensure the body is not null
        body?.let {
            Handler(Looper.getMainLooper()).post {
                // Display a toast message with the sender and body
                Toast.makeText(applicationContext, "$from -> $body", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotification(messageBody: String?) {
        // Ensure the message body is not null
        messageBody?.let {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Using FLAG_IMMUTABLE for PendingIntent to ensure compatibility with API 31+
            val pendingIntent = PendingIntent.getActivity(
                this,
                0, // requestCode
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val channelId = "My Channel ID"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle("My New Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since Android Oreo (API 26), a notification channel is required
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human-readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notificationId = 0
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
