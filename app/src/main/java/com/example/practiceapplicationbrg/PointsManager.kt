package com.example.practiceapplicationbrg

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object PointsManager {
    fun updateUserPoints(firestore: FirebaseFirestore, auth: FirebaseAuth, pointsToAdd: Int, context: Context) {
        val user = auth.currentUser
        user?.let {
            val userRef = firestore.collection("users").document(it.uid)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("points") ?: 0
                val newPoints = currentPoints + pointsToAdd

                // Update the user's points
                transaction.update(userRef, "points", newPoints)

                // Determine the new tier based on points
                val newTier = getTierForPoints(newPoints.toInt())
                if (newTier != snapshot.getString("tier")) {
                    transaction.update(userRef, "tier", newTier)
                }
            }.addOnSuccessListener {
                Toast.makeText(context, "Points and tier updated!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update points/tier: ${e.message}", Toast.LENGTH_LONG).show()
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

