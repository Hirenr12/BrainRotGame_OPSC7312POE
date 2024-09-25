package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userPoints = document.getLong("points")?.toInt() ?: 0
                        updateTiers(userPoints)

                        // Optionally update points/tier here
                        PointsManager.updateUserPoints(firestore, auth, 0, this) // 0 means no points added, but tier gets checked
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load points: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateTiers(points: Int) {
        val tiers = listOf(
            Tier("Fresh Meat", 100, R.drawable.fresh_meat),
            Tier("Infected", 200, R.drawable.infected),
            Tier("Walker", 300, R.drawable.walker),
            Tier("Rotter", 400, R.drawable.rotter),
            Tier("Revenant 5", 500, R.drawable.revenant),
            Tier("Night stalker 6", 600, R.drawable.nightstalker),
            Tier("Necromancer 7", 700, R.drawable.necromancer),
            Tier("Warlord 8", 800, R.drawable.warlord),
            Tier("Dread lord", 900, R.drawable.dreadlord)
        )

        val unlockedTiers = tiers.filter { it.requiredPoints <= points }
        tierAdapter = TierAdapter(unlockedTiers)
        recyclerView.adapter = tierAdapter
    }
}