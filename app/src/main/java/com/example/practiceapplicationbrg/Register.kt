package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get references to the input fields and button
        val usernameField: EditText = findViewById(R.id.usernameField)
        val emailField: EditText = findViewById(R.id.emailField)
        val passwordField: EditText = findViewById(R.id.passwordField)
        val confirmPasswordField: EditText = findViewById(R.id.confirmPasswordField)
        val submitButton: Button = findViewById(R.id.submitButton)

        // Set up the submit button click listener
        submitButton.setOnClickListener {
            registerUser(
                usernameField.text.toString(),
                emailField.text.toString(),
                passwordField.text.toString(),
                confirmPasswordField.text.toString()
            )
        }
    }

    private fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure passwords match
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registration successful, assign 100 initial points
                    val user = auth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, username, email)
                    }
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Login::class.java))
                    finish() // Optional: Close the Register activity
                } else {
                    // Handle registration failure
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, username: String, email: String) {
        // Create a user object with initial 100 points and first tier unlocked
        val userMap = hashMapOf(
            "username" to username,
            "email" to email,
            "points" to 0, // Initial points assigned
            "tier" to "" // Unlock first tier
        )

        // Save user info to Firestore
        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
