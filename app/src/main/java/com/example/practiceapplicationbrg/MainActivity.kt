package com.example.practiceapplicationbrg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val channelID = "My Channel ID"
    private val TAG = "NotificationCheck"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*
        //Firebase Messaging
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
         */


        // Create the notification channel (for Android 8.0+)
        createNotificationChannel()

        // Check and prompt for notification settings
        checkNotificationSettings()




        // Handle window insets (edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the "Sign In" button
        val signInButton: Button = findViewById(R.id.btnSignIn)

        // Set an onClickListener to navigate to the GamePortal activity when the button is clicked
        signInButton.setOnClickListener {
            // Create an Intent to start the GamePortal activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)  // Start the GamePortal activity
        }


        // Find the "Register" button
        val signUpButton: Button = findViewById(R.id.btnRegister)

        // Set an onClickListener to navigate to the Register activity when the button is clicked
        signUpButton.setOnClickListener {
            // Create an Intent to start the Register activity
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
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