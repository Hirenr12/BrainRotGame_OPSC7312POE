package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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