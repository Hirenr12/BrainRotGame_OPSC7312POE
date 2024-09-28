package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomAccountDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etUsername: EditText
    private lateinit var spinnerTier: Spinner
    private lateinit var tierImageView: ImageView
    private lateinit var btnSave: Button

    // Define tiers and their points requirements
    private val tierArray = arrayOf(
        "Fresh Meat", "Infected", "Walker", "Rotter", "Revenant",
        "Nightstalker", "Necromancer", "Warlord", "Dreadlord"
    )

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

    private val avatarUrls = mapOf(
        "Fresh Meat" to "fresh_meat.png",
        "Infected" to "infected.png",
        "Walker" to "walker.png",
        "Rotter" to "rotter.png",
        "Revenant" to "revenant.png",
        "Nightstalker" to "nightstalker.png",
        "Necromancer" to "necromancer.png",
        "Warlord" to "warlord.png",
        "Dreadlord" to "dreadlord.png"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_account_details)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        spinnerTier = findViewById(R.id.spinnerTier)
        tierImageView = findViewById(R.id.tierImageView)
        btnSave = findViewById(R.id.btnSave)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Load current user data
        val user = auth.currentUser
        user?.let {
            loadUserData(it.uid)
        }

        // Set listener for Spinner to change image in real-time
        spinnerTier.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedTier = parent.getItemAtPosition(position).toString()
                updateTierImage(selectedTier)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Save changes to Firestore on button click
        btnSave.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()

            // Update username if not empty
            if (newUsername.isNotEmpty()) {
                saveChangesToFirestore(user!!.uid, newUsername)
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }

            // Always save the avatar customization
            val avatarUrl = getSelectedAvatarUrl()
            saveAvatarToFirestore(user!!.uid, avatarUrl)
        }

    }

    private fun saveAvatarToFirestore(userId: String, avatarUrl: String) {
        val avatarMap = mapOf("imageUrl" to avatarUrl)

        db.collection("users").document(userId).collection("avatars")
            .document("currentAvatar")
            .set(avatarMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Avatar updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update avatar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getSelectedAvatarUrl(): String {
        val selectedTier = spinnerTier.selectedItem.toString()
        return avatarUrls[selectedTier] ?: "" // Return an empty string if not found
    }

    private fun loadUserData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentUsername = document.getString("username")
                    val points = document.getLong("points")?.toInt() ?: 0 // Get user's points
                    etUsername.setText(currentUsername)

                    // Populate the Spinner with tiers based on user's points
                    populateSpinnerWithAllowedTiers(points)

                    // Load the current avatar image
                    loadAvatarImage(userId)
                }
            }
    }

    // Populate the spinner based on the user's points
    private fun populateSpinnerWithAllowedTiers(points: Int) {
        val allowedTiers = tierArray.filter { tier ->
            val requiredPoints = tierPointsMap[tier] ?: 0
            points >= requiredPoints
        }

        // Set the adapter for the spinner with the filtered tiers
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allowedTiers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTier.adapter = adapter
    }

    private fun loadAvatarImage(userId: String) {
        db.collection("users").document(userId).collection("avatars")
            .document("currentAvatar").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val avatarUrl = document.getString("imageUrl")
                    avatarUrl?.let { url ->
                        // Load image using a custom method
                        val imageResId = resources.getIdentifier(url.substringBefore("."),
                            "drawable", packageName)
                        tierImageView.setImageResource(imageResId)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load avatar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTierImage(tier: String) {
        val tierImageRes = when (tier) {
            "Fresh Meat" -> R.drawable.fresh_meat
            "Infected" -> R.drawable.infected
            "Walker" -> R.drawable.walker
            "Rotter" -> R.drawable.rotter
            "Revenant" -> R.drawable.revenant
            "Nightstalker" -> R.drawable.nightstalker
            "Necromancer" -> R.drawable.necromancer
            "Warlord" -> R.drawable.warlord
            "Dreadlord" -> R.drawable.dreadlord
            else -> R.drawable.fresh_meat
        }

        tierImageView.setImageResource(tierImageRes)
    }

    private fun saveChangesToFirestore(userId: String, newUsername: String) {
        val userMap = mutableMapOf<String, Any>()

        // Update only if username is changed
        if (newUsername.isNotEmpty()) {
            userMap["username"] = newUsername
        }

        db.collection("users").document(userId)
            .update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
