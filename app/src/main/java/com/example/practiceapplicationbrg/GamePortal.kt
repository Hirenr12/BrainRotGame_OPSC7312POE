package com.example.practiceapplicationbrg

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceapplicationbrg.FloppyBird.FloppyBird_GameHub
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GamePortal : AppCompatActivity() {

    private lateinit var gameAdapter: GameAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val games = mutableListOf(
        Game("RetroSnake", R.drawable.snake_eater),
        Game("Tic Tac Toe", R.drawable.tic_tac_toe),
        Game("Flappy Bird", R.drawable.flappy_bird),
        Game("Colour Matcher", R.drawable.logocolor),
        Game("???", R.drawable.mystery),
        Game("Players Journal", R.drawable.journal)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_portal)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        gameAdapter = GameAdapter(games, { game ->
            game.isFavorite = !game.isFavorite
            updateGameList()
            saveFavoriteToFirestoreAsync(game)
        }, { game ->
            navigateToGameDetails(game)
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = gameAdapter

        checkMysteryGameUnlockStatusAsync() // Check mystery game unlock status
        loadFavoritesFromFirestoreAsync()  // Load favorite games when the activity starts
    }

    private fun loadFavoritesFromFirestoreAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val querySnapshot = firestore.collection("users").document(userId)
                    .collection("favorites").get().await()

                val favoriteTitles = querySnapshot.documents.map { it.getString("title") ?: "" }

                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    games.forEach { game ->
                        game.isFavorite = favoriteTitles.contains(game.title)
                    }
                    updateGameList() // Refresh the game list to reflect favorite status
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GamePortal, "Failed to load favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveFavoriteToFirestoreAsync(game: Game) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val favoriteRef = firestore.collection("users").document(userId)
                    .collection("favorites").document(game.title)

                if (game.isFavorite) {
                    // Save the game as favorite
                    favoriteRef.set(hashMapOf("title" to game.title, "imageResId" to game.imageResId)).await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GamePortal, "${game.title} added to favorites", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Remove the game from favorites
                    favoriteRef.delete().await()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GamePortal, "${game.title} removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GamePortal, "Failed to save favorite: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkMysteryGameUnlockStatusAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = auth.currentUser
                user?.let {
                    val document = firestore.collection("users").document(it.uid).get().await()
                    val points = document.getLong("points") ?: 0
                    val isMysteryGameUnlocked = document.getBoolean("mysteryGameUnlocked") ?: false

                    withContext(Dispatchers.Main) {
                        if (points >= 500 && !isMysteryGameUnlocked) {
                            unlockMysteryGameAsync(it.uid)
                        } else if (isMysteryGameUnlocked) {
                            updateMysteryGame()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GamePortal, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun unlockMysteryGameAsync(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                firestore.collection("users").document(userId)
                    .update("mysteryGameUnlocked", true).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GamePortal, "Congratulations! You've unlocked your first mystery game!", Toast.LENGTH_LONG).show()
                    updateMysteryGame()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GamePortal, "Failed to unlock the mystery game: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateGameList() {
        games.sortByDescending { it.isFavorite } // Sort by favorites
        gameAdapter.notifyDataSetChanged()       // Notify adapter of changes
    }

    private fun navigateToGameDetails(game: Game) {
        val intent = Intent(this, when (game.title) {
            "RetroSnake" -> SnakeGameActivity::class.java
            "Tic Tac Toe" -> TikTakToeDecriptionActivity::class.java
            "Colour Matcher" -> ColorMatchGame::class.java
            "Flappy Bird" -> FloppyBird_GameHub::class.java
            "Retro Brick Breaker" -> RetroBrickBreakerActivity::class.java
            "Players Journal" -> ActivityPlayersJournal::class.java
            else -> GamePortal::class.java
        })
        startActivity(intent)
    }

    private fun updateMysteryGame() {
        val mysteryGame = games.find { it.title == "???" }
        mysteryGame?.let {
            it.title = "Retro Brick Breaker"
            it.imageResId = R.drawable.retrobrickbreaker
            updateGameList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, AccountDetailsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_leader_board -> {
                val intent = Intent(this, GlobalLeaderboardActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_private_leader_board -> {
                val intent = Intent(this, PrivateLeaderboardActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_journal -> {
                val intent = Intent(this, ActivityPlayersJournal::class.java)
                startActivity(intent)
                true
            }
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
