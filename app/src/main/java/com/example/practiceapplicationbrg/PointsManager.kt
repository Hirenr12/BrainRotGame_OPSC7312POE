package com.example.practiceapplicationbrg

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object PointsManager {
    fun updateUserPoints(firestore: FirebaseFirestore, auth: FirebaseAuth, pointsToAdd: Int, context: Context) {
        val user = auth.currentUser
        user?.let {
            val userRef = firestore.collection("users").document(it.uid)

            // Retrieve current points (fallback to cache if needed)
            userRef.get()
                .addOnSuccessListener { snapshot ->
                    val currentPoints = snapshot.getLong("points") ?: 0
                    val newPoints = currentPoints + pointsToAdd

                    // Prepare data for updating points and tier
                    val updateData = hashMapOf(
                        "points" to newPoints,
                        "tier" to getTierForPoints(newPoints.toInt())
                    )

                    // Update the user's points and tier, works offline too
                    userRef.set(updateData, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Points and tier updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to update points/tier: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to retrieve points: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Function to determine the tier based on points
    private fun getTierForPoints(points: Int): String {
        return when {
            points >= 900 -> "Dread lord"
            points >= 800 -> "Warlord"
            points >= 700 -> "Necromancer"
            points >= 600 -> "Night stalker"
            points >= 500 -> "Revenant"
            points >= 400 -> "Rotter"
            points >= 300 -> "Walker"
            points >= 200 -> "Infected"
            points >= 100 -> "Fresh Meat"
            else -> "Newbie"
        }
    }
}
