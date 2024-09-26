package com.example.practiceapplicationbrg

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalLeaderboardActivity : AppCompatActivity() {

    private lateinit var gameComboBox: AutoCompleteTextView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val gamesList = mutableListOf<String>()
    private lateinit var apiService: ApiService

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        setContentView(R.layout.activity_global_leaderboard)

        // Initialize the RecyclerView before using it
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        setupRecyclerView()  // Now this will not throw an error

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameComboBox = findViewById(R.id.gameComboBox)

        setupRetrofit()
        fetchGames()

        gameComboBox.setOnItemClickListener { parent, _, position, _ ->
            val selectedGame = parent.getItemAtPosition(position).toString()
            Log.d("GlobalLeaderboardActivity", "Selected game: $selectedGame") // Log selected game
            fetchLeaderboard(selectedGame)
        }
    }


    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(emptyList())
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

                    // Ensure the dropdown shows when the box is clicked or gains focus
                    gameComboBox.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            gameComboBox.showDropDown()  // Show dropdown when it gains focus
                        }
                    }

                    gameComboBox.setOnClickListener {
                        if (gameComboBox.adapter != null) {
                            gameComboBox.showDropDown()  // Ensure dropdown shows on click
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
                // Sort the entries in descending order based on HighScore
                val sortedEntries = leaderboardEntries.sortedByDescending { it.HighScore }
                withContext(Dispatchers.Main) {
                    leaderboardAdapter.updateData(sortedEntries)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Error fetching leaderboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
