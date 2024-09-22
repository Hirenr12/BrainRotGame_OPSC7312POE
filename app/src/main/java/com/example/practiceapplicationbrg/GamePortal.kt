package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class GamePortal : AppCompatActivity() {

    private lateinit var gameAdapter: GameAdapter
    private val games = mutableListOf(
        Game("Snake Eater", R.drawable.snake_eater),
        Game("Tic Tac Toe", R.drawable.tic_tac_toe),
        Game("Hang Man", R.drawable.hang_man),
        Game("Flappy Bird", R.drawable.flappy_bird),
        Game("???", R.drawable.mystery)
    )

    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_portal)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the GameAdapter
        gameAdapter = GameAdapter(games, { game ->
            game.isFavorite = !game.isFavorite
            updateGameList()
        }, { game ->
            // Handle game item click to navigate to the respective activity
            navigateToGameDetails(game)
        })

        // Set up the RecyclerView with a GridLayoutManager (2 columns)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = gameAdapter

        // Apply window insets for immersive UI (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Method to update the game list, sorting by favorites first
    private fun updateGameList() {
        games.sortByDescending { it.isFavorite }
        gameAdapter.notifyDataSetChanged()
    }

    // Inflate the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Handle Profile navigation
                true
            }
            R.id.action_settings -> {
                // Navigate to SettingsActivity
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_daily_challenge -> {
                // Handle Daily Challenge navigation
                true
            }
            R.id.action_leader_board -> {
                // Handle Leader Board navigation
                true
            }
            R.id.action_sign_out -> {
                // Handle Sign Out action
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        // Sign out from Firebase
        auth.signOut()

        // Redirect to Login activity
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear stack
        startActivity(intent)
        finish() // Close the current activity
    }

    // Method to navigate to the details page of the selected game
    private fun navigateToGameDetails(game: Game) {
        val intent = Intent(this, when (game.title) {
            "Snake Eater" -> ActivityPlayersJournal::class.java
//            "Tic Tac Toe" -> TicTacToeActivity::class.java
//            "Hang Man" -> HangManActivity::class.java
//            "Flappy Bird" -> FlappyBirdActivity::class.java
            // Add other game titles and their corresponding activities here
           else -> GamePortal::class.java // Fallback activity
        })
        startActivity(intent)
    }
}
