package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommunityActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var tipEditText: EditText
    private lateinit var submitTipButton: Button
    private lateinit var tipLayout: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find views by ID
        usernameEditText = findViewById(R.id.usernameEditText)
        tipEditText = findViewById(R.id.tipEditText)
        submitTipButton = findViewById(R.id.submitTipButton)
        tipLayout = findViewById(R.id.tipLayout)

        // Load the logged-in user's username
        loadLoggedInUsername()

        // Load all tips from Firestore
        getAllTipsFromFirestore()

        // Set up the submit button
        submitTipButton.setOnClickListener {
            val content = tipEditText.text.toString()
            val username = usernameEditText.text.toString()

            if (content.isNotEmpty()) {
                addTipToFirestore(username, content)
            } else {
                Toast.makeText(this, "Please enter a tip", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load the logged-in user's username from Firestore
    private fun loadLoggedInUsername() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Query the user's information from the 'users' collection
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        usernameEditText.setText(username) // Automatically fill the username field
                        usernameEditText.isEnabled = false // Disable editing the username
                    } else {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load username", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Add tip to Firestore
    private fun addTipToFirestore(username: String, content: String) {
        val tip = hashMapOf(
            "username" to username,
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("tips")
            .add(tip)
            .addOnSuccessListener {
                Toast.makeText(this, "Tip added successfully", Toast.LENGTH_SHORT).show()
                tipEditText.text.clear()
                getAllTipsFromFirestore() // Reload tips to show the new one
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add tip", Toast.LENGTH_SHORT).show()
            }
    }

    // Get all tips from Firestore
    private fun getAllTipsFromFirestore() {
        firestore.collection("tips")
            .orderBy("timestamp")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error fetching tips", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                tipLayout.removeAllViews() // Clear previous views

                // Iterate through the tips and create TextViews for each
                value?.forEach { doc ->
                    val username = doc.getString("username") ?: "Unknown"
                    val content = doc.getString("content") ?: ""

                    val tipView = TextView(this).apply {
                        text = "$username: $content"
                        textSize = 16f
                        setPadding(8, 8, 8, 8)
                    }

                    tipLayout.addView(tipView)
                }
            }
    }
}
