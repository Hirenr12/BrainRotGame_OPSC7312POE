package com.example.practiceapplicationbrg

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GlobalLeaderboardActivity : AppCompatActivity() {

    private lateinit var gameComboBox: AutoCompleteTextView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val gamesList = mutableListOf<String>()
    private lateinit var apiService: ApiService
    private var currentUserUsername: String? = null
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var leaderboardImage: ImageView
    private lateinit var headingRow: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_global_leaderboard)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        setupRecyclerView()

        leaderboardImage = findViewById(R.id.leaderboardImage) // Initialize ImageView
        headingRow = findViewById(R.id.headingRow) // Initialize heading row

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameComboBox = findViewById(R.id.gameComboBox)

        setupRetrofit()
        fetchCurrentUserUsername()
        fetchGames()

        gameComboBox.setOnItemClickListener { parent, _, position, _ ->
            val selectedGame = parent.getItemAtPosition(position).toString()
            Log.d("GlobalLeaderboardActivity", "Selected game: $selectedGame")
            fetchLeaderboard(selectedGame)
        }
    }

    private fun fetchCurrentUserUsername() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("GlobalLeaderboardActivity", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("GlobalLeaderboardActivity", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GlobalLeaderboardActivity, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("GlobalLeaderboardActivity", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(emptyList()) { selectedUser ->
            Log.d("GlobalLeaderboardActivity", "User clicked: $selectedUser")
            val selectedGame = gameComboBox.text.toString()
            currentUserUsername?.let { currentUser ->
                if (selectedGame.isNotEmpty()) {
                    addToPrivateLeaderboard(currentUser, selectedUser, selectedGame)
                } else {
                    Log.e("GlobalLeaderboardActivity", "selectedGame is empty!")
                }
            } ?: Log.e("GlobalLeaderboardActivity", "currentUserUsername is null!")
        }
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.adapter = leaderboardAdapter
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun fetchGames() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val games = apiService.getAllGames()
                Log.d("GlobalLeaderboardActivity", "Fetched games: $games")
                gamesList.clear()
                gamesList.addAll(games)

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        this@GlobalLeaderboardActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        gamesList
                    )

                    gameComboBox.setAdapter(adapter)
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
                }
            } catch (e: Exception) {
                Log.e("GlobalLeaderboardActivity", "Error fetching games", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Error fetching games", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchLeaderboard(selectedGame: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val leaderboardEntries = apiService.getLeaderboard(selectedGame)
                val sortedEntries = leaderboardEntries.sortedByDescending { it.HighScore }
                withContext(Dispatchers.Main) {
                    leaderboardAdapter.updateData(sortedEntries)
                }
            } catch (e: Exception) {
                Log.e("GlobalLeaderboardActivity", "Error fetching leaderboard", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Error fetching leaderboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addToPrivateLeaderboard(currentUser: String, selectedUser: String, gameName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("GlobalLeaderboardActivity", "Game Name: $gameName, Current User: $currentUser, Selected User: $selectedUser")

                // Create the request object
                val request = AddToPrivateLeaderboardRequest(
                    gameName = gameName,
                    currentUser = currentUser,
                    selectedUser = selectedUser
                )

                // Use the modified API call
                val response = apiService.addToPrivateLeaderboard(gameName, request)

                if (response.isSuccessful) {
                    Log.d("GlobalLeaderboardActivity", "User added to private leaderboard successfully.")

                    // Show success toast message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@GlobalLeaderboardActivity,
                            "User $selectedUser added to private leaderboard!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Log.e("GlobalLeaderboardActivity", "Failed to add user: ${response.errorBody()?.string()}")

                    // Optionally, show failure toast message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@GlobalLeaderboardActivity,
                            "Failed to add user to private leaderboard.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } catch (e: Exception) {
                Log.e("GlobalLeaderboardActivity", "Error adding user to private leaderboard", e)

                // Optionally, show error toast message
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GlobalLeaderboardActivity,
                        "Error adding user to private leaderboard.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }




}
