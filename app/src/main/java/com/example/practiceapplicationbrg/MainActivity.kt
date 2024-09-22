package com.example.practiceapplicationbrg

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


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
}