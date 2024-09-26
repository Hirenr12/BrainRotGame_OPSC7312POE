package com.example.practiceapplicationbrg

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
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val currentUsername = document.getString("username")
                        val currentTier = document.getString("tier")
                        etUsername.setText(currentUsername)

                        // Set spinner to current tier
                        val tierArray = resources.getStringArray(R.array.tier_array)
                        val index = tierArray.indexOf(currentTier)
                        spinnerTier.setSelection(index)

                        // Set avatar image
                        updateTierImage(currentTier ?: "Fresh Meat")
                    }
                }
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
            val selectedTier = spinnerTier.selectedItem.toString()

            if (newUsername.isNotEmpty()) {
                saveChangesToFirestore(user!!.uid, newUsername, selectedTier)
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
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

    private fun saveChangesToFirestore(userId: String, newUsername: String, newTier: String) {
        val userMap = mapOf(
            "username" to newUsername,
            "tier" to newTier
        )

        db.collection("users").document(userId)
            .update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save changes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
