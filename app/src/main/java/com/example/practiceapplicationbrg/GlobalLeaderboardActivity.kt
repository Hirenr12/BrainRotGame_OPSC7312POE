package com.example.practiceapplicationbrg

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class GlobalLeaderboardActivity : AppCompatActivity() {

    private lateinit var gameComboBox: AutoCompleteTextView
    private lateinit var leaderboardRecyclerView: RecyclerView
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val gamesList = mutableSetOf<String>() // Using a Set to avoid duplicates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_global_leaderboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameComboBox = findViewById(R.id.gameComboBox)
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView)

        setupRecyclerView()
        fetchGames()

        gameComboBox.setOnItemClickListener { parent, view, position, id ->
            val selectedGame = parent.getItemAtPosition(position).toString()
            fetchLeaderboard(selectedGame)
        }
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(emptyList())
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)
        leaderboardRecyclerView.adapter = leaderboardAdapter
    }

    private fun fetchGames() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://<YOUR_API_URL>/leaderboard/global/allGames")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Failed to fetch games", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { responseBody ->
                    val games = parseGames(responseBody)
                    runOnUiThread {
                        gamesList.addAll(games)
                        val adapter = ArrayAdapter(this@GlobalLeaderboardActivity, android.R.layout.simple_dropdown_item_1line, gamesList.toList())
                        gameComboBox.setAdapter(adapter)
                    }
                }
            }
        })
    }

    private fun parseGames(jsonResponse: String): List<String> {
        val games = mutableListOf<String>()
        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val gameName = jsonArray.getJSONObject(i).getString("GameName")
            games.add(gameName)
        }
        return games.distinct()
    }

    private fun fetchLeaderboard(selectedGame: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://<YOUR_API_URL>/leaderboard/global/$selectedGame")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@GlobalLeaderboardActivity, "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { responseBody ->
                    val leaderboardEntries = parseLeaderboard(responseBody)
                    runOnUiThread {
                        leaderboardAdapter.updateData(leaderboardEntries)
                    }
                }
            }
        })
    }

    private fun parseLeaderboard(jsonResponse: String): List<LeaderboardEntry> {
        val leaderboard = mutableListOf<LeaderboardEntry>()
        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val username = jsonObject.getString("Username")
            val score = jsonObject.getInt("HighScore")
            leaderboard.add(LeaderboardEntry(username, score))
        }
        return leaderboard
    }
}
