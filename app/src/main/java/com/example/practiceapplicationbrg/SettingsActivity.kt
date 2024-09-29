package com.example.practiceapplicationbrg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class SettingsActivity : AppCompatActivity() {

    private val channelID = "My Channel ID"
    private val TAG = "NotificationCheck"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set up buttons
        val accountDetailsButton = findViewById<Button>(R.id.button_account_details)
        val privacyPolicyButton = findViewById<Button>(R.id.button_privacy_policy)
        val notificationsButton = findViewById<Button>(R.id.button_notifications)
        val supportFeatureButton = findViewById<Button>(R.id.button_support_feature)
        val communityButton = findViewById<Button>(R.id.button_community_activity)

        communityButton.setOnClickListener {
            // Account Details button click
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

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

            // Create the notification channel (for Android 8.0+)
            createNotificationChannel()

            // Check and prompt for notification settings
            checkNotificationSettings()
        }

        supportFeatureButton.setOnClickListener {
            Log.d("REDIRECT", "Redirecting to page")
            // Support and Feedback button click
            val intent = Intent(this, SupportFeatureActivity::class.java)
            startActivity(intent)
            Log.d("REDIRECT", "Fail in Redirecting to page")
        }

    }














    private fun createNotificationChannel() {
        // Only for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notifications"
            val descriptionText = "Channel for my app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d(TAG, "Notification channel created")
        }
    }






    private fun checkNotificationSettings() {
        val notificationManagerCompat = NotificationManagerCompat.from(this)

        // 1. Check if global notifications are disabled
        if (!notificationManagerCompat.areNotificationsEnabled()) {
            Log.d(TAG, "Global notifications are disabled")
            showDeviceNotificationSettingsDialog()
            return
        }

        // 2. For Android 8.0+ (API 26+), check the notification channel status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel: NotificationChannel? = notificationManager.getNotificationChannel(channelID)

            if (channel == null) {
                Log.d(TAG, "Notification channel does not exist")
                showAppNotificationSettingsDialog()
                return
            }

            // If channel exists, check if it's disabled
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                Log.d(TAG, "Notification channel is disabled")
                showAppNotificationSettingsDialog()
                return
            }
        }

        // If everything is enabled
        Log.d(TAG, "All notifications are properly enabled")
    }







    private fun showDeviceNotificationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Notifications")
            .setMessage("Please enable notifications in your device settings to receive updates.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Open device-wide notification settings
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        Log.d(TAG, "Displayed global notification settings dialog")
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

        Log.d(TAG, "Displayed app-specific notification settings dialog")
    }

}
