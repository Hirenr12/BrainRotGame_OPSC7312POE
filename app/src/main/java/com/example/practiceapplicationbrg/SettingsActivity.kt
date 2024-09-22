package com.example.practiceapplicationbrg

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class SettingsActivity : AppCompatActivity() {

    private val channelID = "My Channel ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set up buttons
        val accountDetailsButton = findViewById<Button>(R.id.button_account_details)
        val privacyPolicyButton = findViewById<Button>(R.id.button_privacy_policy)
        val notificationsButton = findViewById<Button>(R.id.button_notifications)
        val supportFeedbackButton = findViewById<Button>(R.id.button_support_feedback)

        accountDetailsButton.setOnClickListener {
            // Account Details button click
            val intent = Intent(this, AccountDetailsActivity::class.java)
            startActivity(intent)
        }

        privacyPolicyButton.setOnClickListener {
            // Privacy Policy button click
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        notificationsButton.setOnClickListener {
            // Notifications button click
            checkAndHandleNotifications()
        }

        supportFeedbackButton.setOnClickListener {
            // Support and Feedback button click
            val intent = Intent(this, SupportFeedbackActivity::class.java)
            startActivity(intent)
        }
    }



    private fun checkAndHandleNotifications() {
        // Check if notifications are enabled
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            showDeviceNotificationSettingsDialog()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationManagerCompat.from(this).getNotificationChannel(channelID)
            if (channel == null || channel.importance == NotificationManager.IMPORTANCE_NONE) {
                showAppNotificationSettingsDialog()
            } else {
                // If notifications are enabled, show a toast message
                showNotificationEnabledMessage()
            }
        } else {
            // For devices below Android O, simply show enabled message if notifications are enabled
            showNotificationEnabledMessage()
        }
    }

    private fun showNotificationEnabledMessage() {
        Toast.makeText(this, "Notifications already enabled", Toast.LENGTH_SHORT).show()
    }

    private fun showDeviceNotificationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notifications Disabled")
            .setMessage("Please enable notifications for this app to receive notifications.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                })
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAppNotificationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("App Notifications Disabled")
            .setMessage("Please enable notifications for this app to receive notifications.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, channelID)
                })
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
