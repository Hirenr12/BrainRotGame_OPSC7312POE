package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
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
        }

        supportFeedbackButton.setOnClickListener {
            // Support and Feedback button click
            val intent = Intent(this, SupportFeedbackActivity::class.java)
            startActivity(intent)
        }
    }
}
