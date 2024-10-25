package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source

class ActivityPlayersJournal : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tierAdapter: TierAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_journal)

        recyclerView = findViewById(R.id.TierRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Enable Firestore offline persistence
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        val user = auth.currentUser
        user?.let {
            val docRef = firestore.collection("users").document(it.uid)

            // First, try fetching from the server, fallback to cache if offline
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userPoints = document.getLong("points")?.toInt() ?: 0
                        updateTiers(userPoints)

                        // Call PointsManager to update the points and tier
                        PointsManager.updateUserPoints(firestore, auth, 0, this) // Use 'this' for the activity context
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Try fetching from the cache if the server call fails
                    docRef.get(Source.CACHE)
                        .addOnSuccessListener { cachedDoc ->
                            if (cachedDoc != null && cachedDoc.exists()) {
                                userPoints = cachedDoc.getLong("points")?.toInt() ?: 0
                                updateTiers(userPoints)
                            } else {
                                Toast.makeText(this, "No cached data available", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }

    private fun updateTiers(points: Int) {
        val tiers = listOf(
            Tier("Fresh Meat", 100, R.drawable.fresh_meat),
            Tier("Infected", 200, R.drawable.infected),
            Tier("Walker", 300, R.drawable.walker),
            Tier("Rotter", 400, R.drawable.rotter),
            Tier("Revenant", 500, R.drawable.revenant),
            Tier("Night stalker", 600, R.drawable.nightstalker),
            Tier("Necromancer", 700, R.drawable.necromancer),
            Tier("Warlord", 800, R.drawable.warlord),
            Tier("Dread lord", 900, R.drawable.dreadlord)
        )

        val unlockedTiers = tiers.filter { it.requiredPoints <= points }
        tierAdapter = TierAdapter(unlockedTiers)
        recyclerView.adapter = tierAdapter
    }
}
