package com.example.practiceapplicationbrg

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import android.net.ConnectivityManager
import android.content.Context
import android.net.NetworkInfo

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

            // Check network connectivity
            if (!isNetworkAvailable()) {
                // Inform the user that the app is in offline mode
                Toast.makeText(this, "Fetching data in offline mode...", Toast.LENGTH_SHORT).show()
            }

            // First, try fetching from the server, fallback to cache if offline
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        userPoints = document.getLong("points")?.toInt() ?: 0
                        updateTiers(userPoints)

                        // Call PointsManager to update the points and tier
                        PointsManager.updateUserPoints(firestore, auth, 0, this) // Use 'this' for the activity context

                        // Log success
                        if (BuildConfig.DEBUG) {
                            Log.d("ActivityPlayersJournal", "User points fetched successfully: $userPoints")
                        }
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                        if (BuildConfig.DEBUG) {
                            Log.w("ActivityPlayersJournal", "No user data found for document: ${docRef.path}")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Try fetching from the cache if the server call fails
                    docRef.get(Source.CACHE)
                        .addOnSuccessListener { cachedDoc ->
                            if (cachedDoc != null && cachedDoc.exists()) {
                                userPoints = cachedDoc.getLong("points")?.toInt() ?: 0
                                updateTiers(userPoints)

                                // Log cache success
                                if (BuildConfig.DEBUG) {
                                    Log.d("ActivityPlayersJournal", "User points fetched from cache: $userPoints")
                                }
                            } else {
                                Toast.makeText(this, "No cached data available", Toast.LENGTH_SHORT).show()
                                if (BuildConfig.DEBUG) {
                                    Log.w("ActivityPlayersJournal", "No cached data found for document: ${docRef.path}")
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                            if (BuildConfig.DEBUG) {
                                Log.e("ActivityPlayersJournal", "Failed to load data: ${e.message}")
                            }
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

        // Log tier update
        if (BuildConfig.DEBUG) {
            Log.d("ActivityPlayersJournal", "Tiers updated: ${unlockedTiers.map { it.name }}")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }
}
