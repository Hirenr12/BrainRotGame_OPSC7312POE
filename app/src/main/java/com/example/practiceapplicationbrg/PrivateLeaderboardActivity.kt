package com.example.practiceapplicationbrg

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.tasks.await

class PrivateLeaderboardActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var gameComboBox: AutoCompleteTextView
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var currentUserUsername: String = "Default Username" // Default value for currentUserUsername

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_leaderboard)

        // Initialize Retrofit for API calls
        val retrofit = Retrofit.Builder()
            .baseUrl("https://brainrotapi.ue.r.appspot.com/") // Ensure this points to your local server
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Setup RecyclerView
        recyclerView = findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize LeaderboardAdapter with a click listener for private leaderboard items
        leaderboardAdapter = LeaderboardAdapter(mutableListOf()) { selectedUsername ->
            // Handle user click on leaderboard entry
            Log.d("PrivateLeaderboard", "Clicked on user: $selectedUsername")
        }
        recyclerView.adapter = leaderboardAdapter

        // Setup game ComboBox
        gameComboBox = findViewById(R.id.gameComboBox)
        loadGameNames()

        // Handle game selection from ComboBox
        gameComboBox.setOnItemClickListener { parent, _, position, _ ->
            val selectedGame = parent.getItemAtPosition(position) as String
            Log.d("PrivateLeaderboard", "Selected game: $selectedGame")
            loadPrivateLeaderboardForGame(selectedGame)
        }
    }

    private fun loadGameNames() {
        lifecycleScope.launch {
            try {
                val games = apiService.getAllGames()
                Log.d("PrivateLeaderboard", "Games loaded: $games")
                val adapter = ArrayAdapter(
                    this@PrivateLeaderboardActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    games
                )
                gameComboBox.setAdapter(adapter)

                // Show dropdown when ComboBox is clicked or focused
                gameComboBox.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        gameComboBox.showDropDown()
                    }
                }

                gameComboBox.setOnClickListener {
                    if (gameComboBox.adapter != null) {
                        gameComboBox.showDropDown()
                    }
                }

            } catch (e: Exception) {
                Log.e("PrivateLeaderboard", "Error loading games: ${e.message}")
            }
        }
    }

    private fun loadPrivateLeaderboardForGame(gameName: String) {
        lifecycleScope.launch {
            try {
                Log.d("PrivateLeaderboard", "Loading private leaderboard for game: $gameName and user: $currentUserUsername")

                // Fetch current user username from Firestore
                currentUserUsername = fetchCurrentUserUsername() ?: currentUserUsername // Use the fetched username or keep default
                Log.d("PrivateLeaderboard", "Fetched current user username: $currentUserUsername")

                // Make API call to get private leaderboard entries
                val leaderboardEntries = apiService.getPrivateLeaderboard(gameName, currentUserUsername)
                Log.d("PrivateLeaderboard", "Fetched leaderboard entries: $leaderboardEntries")

                // Update the adapter with the fetched private leaderboard entries
                leaderboardAdapter.updateData(leaderboardEntries)
            } catch (e: Exception) {
                Log.e("PrivateLeaderboard", "Error loading private leaderboard: ${e.message}")
            }
        }
    }

    private fun getCurrentUser(): String {
        // Use FirebaseAuth to get the currently logged-in user's username
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.displayName ?: "UnknownUser"
    }

    private suspend fun fetchCurrentUserUsername(): String? {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        return if (currentUserUid != null) {
            val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.getString("username")
            } else {
                Log.e("PrivateLeaderboard", "User document not found")
                null
            }
        } else {
            Log.e("PrivateLeaderboard", "No current user logged in")
            null
        }
    }
}
