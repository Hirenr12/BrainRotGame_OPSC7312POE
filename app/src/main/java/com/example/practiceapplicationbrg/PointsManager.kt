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
                transaction.update(userRef, "points", newPoints)
            }.addOnSuccessListener {
                Toast.makeText(context, "Points updated!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to update points: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
