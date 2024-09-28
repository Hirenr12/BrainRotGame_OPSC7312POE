package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvUsername: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvTier: TextView
    private lateinit var tierImageView: ImageView
    private lateinit var btnCustomize: Button

    // Tier mapping based on points
    private val tierPointsMap = mapOf(
        "Fresh Meat" to 0,
        "Infected" to 200,
        "Walker" to 300,
        "Rotter" to 400,
        "Revenant" to 500,
        "Nightstalker" to 600,
        "Necromancer" to 700,
        "Warlord" to 800,
        "Dreadlord" to 900
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        // Initialize views
        tvUsername = findViewById(R.id.tvUsername)
        tvPoints = findViewById(R.id.tvPoints)
        tvTier = findViewById(R.id.tvTier)
        tierImageView = findViewById(R.id.tierImageView)
        btnCustomize = findViewById(R.id.btnCustomize)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set the button click listener to navigate to CustomAccountDetailsActivity
        btnCustomize.setOnClickListener {
            val intent = Intent(this, CustomAccountDetailsActivity::class.java)
            startActivity(intent)
        }

        // Fetch user data when the activity is created
        fetchUserData()
    }

    override fun onResume() {
        super.onResume()

        // Fetch user data from Firestore when the activity is resumed
        fetchUserData()
    }

    // Function to fetch user data and update UI
    private fun fetchUserData() {
        val user = auth.currentUser
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val points = document.getLong("points")?.toInt() ?: 0

                        // Calculate and update the tier based on points
                        val userTier = calculateUserTier(points)
                        tvUsername.text = "Username: $username"
                        tvPoints.text = "Points: $points"
                        tvTier.text = "Tier: $userTier"

                        // Update the tier in Firestore if needed
                        updateTierInFirestore(it.uid, userTier)

                        // Fetch and update avatar image based on tier
                        updateAvatarImage(it.uid)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure of fetching user data
                }
        }
    }

    // Function to calculate the user's tier based on their points
    private fun calculateUserTier(points: Int): String {
        return tierPointsMap.entries
            .filter { points >= it.value } // Filter only the tiers that the user qualifies for
            .maxByOrNull { it.value }?.key ?: "Fresh Meat" // Get the highest tier or "Fresh Meat" as default
    }

    // Function to update the tier in Firestore if it's different from the current value
    private fun updateTierInFirestore(userId: String, userTier: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val currentTier = document.getString("tier")
                if (currentTier != userTier) {
                    db.collection("users").document(userId)
                        .update("tier", userTier)
                        .addOnSuccessListener {
                            // Tier updated successfully
                        }
                        .addOnFailureListener { e ->
                            // Handle failure to update tier
                        }
                }
            }
    }

    // Function to fetch and update the avatar image based on the current avatar stored in Firestore
    private fun updateAvatarImage(userId: String) {
        db.collection("users").document(userId)
            .collection("avatars").document("currentAvatar").get()
            .addOnSuccessListener { avatarDoc ->
                if (avatarDoc != null) {
                    val imageName = avatarDoc.getString("imageUrl")
                    if (!imageName.isNullOrEmpty()) {
                        val resourceName = imageName.substringBefore(".")
                        val imageResId = resources.getIdentifier(resourceName, "drawable", packageName)
                        if (imageResId != 0) {
                            tierImageView.setImageResource(imageResId)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure of fetching avatar image name
            }
    }
}
