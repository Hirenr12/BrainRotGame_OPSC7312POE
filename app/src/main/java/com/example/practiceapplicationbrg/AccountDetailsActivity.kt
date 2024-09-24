package com.example.practiceapplicationbrg

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        // Initialize views
        tvUsername = findViewById(R.id.tvUsername)
        tvPoints = findViewById(R.id.tvPoints)
        tvTier = findViewById(R.id.tvTier)
        tierImageView = findViewById(R.id.tierImageView)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val points = document.getLong("points")?.toInt() ?: 0
                        val tier = document.getString("tier")

                        // Update UI with user data
                        tvUsername.text = "Username: $username"
                        tvPoints.text = "Points: $points"
                        tvTier.text = "Tier: $tier"

                        // Update the Tier Image based on points
                        updateTierImage(points)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }

    private fun updateTierImage(points: Int) {
        val tierImage = when {
            points >= 900 -> R.drawable.dreadlord
            points >= 800 -> R.drawable.warlord
            points >= 700 -> R.drawable.necromancer
            points >= 600 -> R.drawable.nightstalker
            points >= 500 -> R.drawable.revenant
            points >= 400 -> R.drawable.rotter
            points >= 300 -> R.drawable.walker
            points >= 200 -> R.drawable.infected
            else -> R.drawable.fresh_meat
        }

        // Set the appropriate image
        tierImageView.setImageResource(tierImage)
    }
}
