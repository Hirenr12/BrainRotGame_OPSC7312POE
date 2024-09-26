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

                        // Fetch the avatar image name (without extension)
                        db.collection("users").document(it.uid)
                            .collection("avatars").document("currentAvatar").get()
                            .addOnSuccessListener { avatarDoc ->
                                if (avatarDoc != null) {
                                    val imageName = avatarDoc.getString("imageUrl")
                                    if (!imageName.isNullOrEmpty()) {
                                        // Remove the file extension (e.g., "fresh_meat.png" -> "fresh_meat")
                                        val resourceName = imageName.substringBefore(".")
                                        // Get the drawable resource ID by name
                                        val imageResId = resources.getIdentifier(resourceName, "drawable", packageName)

                                        if (imageResId != 0) {
                                            // Set the drawable to the ImageView
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
                .addOnFailureListener { e ->
                    // Handle failure of fetching user data
                }
        }
    }
}
