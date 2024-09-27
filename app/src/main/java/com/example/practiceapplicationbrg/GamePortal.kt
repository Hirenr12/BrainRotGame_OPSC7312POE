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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GamePortal : AppCompatActivity() {

    private lateinit var gameAdapter: GameAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val games = mutableListOf(
        Game("Snake Eater", R.drawable.snake_eater),
        Game("Tic Tac Toe", R.drawable.tic_tac_toe),
        Game("Hang Man", R.drawable.hang_man),
        Game("Flappy Bird", R.drawable.flappy_bird),
        Game("???", R.drawable.mystery)
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
            saveFavoriteToFirestore(game)
        }, { game ->
            navigateToGameDetails(game)
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = gameAdapter

        checkMysteryGameUnlockStatus() // Check mystery game unlock status
        loadFavoritesFromFirestore()  // Load favorite games when the activity starts
    }

    private fun loadFavoritesFromFirestore() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("favorites")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val favoriteTitles = querySnapshot.documents.map { it.getString("title") ?: "" }
                // Mark the games as favorites if they are in Firestore
                games.forEach { game ->
                    game.isFavorite = favoriteTitles.contains(game.title)
                }
                updateGameList() // Refresh the game list to reflect favorite status
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load favorites: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGameList() {
        games.sortByDescending { it.isFavorite } // Sort by favorites
        gameAdapter.notifyDataSetChanged()       // Notify adapter of changes
    }

    private fun saveFavoriteToFirestore(game: Game) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection("users").document(userId).collection("favorites").document(game.title)

        if (game.isFavorite) {
            // Save the game as favorite
            favoriteRef.set(hashMapOf("title" to game.title, "imageResId" to game.imageResId))
                .addOnSuccessListener {
                    Toast.makeText(this, "${game.title} added to favorites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to save favorite: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Remove the game from favorites
            favoriteRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "${game.title} removed from favorites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to remove favorite: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
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
//            R.id.action_daily_challenge -> {
//                true
//            }
            R.id.action_leader_board -> {
                val intent = Intent(this, GlobalLeaderboardActivity::class.java)
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
    private fun navigateToGameDetails(game: Game) {
        val intent = Intent(this, when (game.title) {
            "Snake Eater" -> ActivityPlayersJournal::class.java
//            "Tic Tac Toe" -> TicTacToeActivity::class.java
//            "Hang Man" -> HangManActivity::class.java
//            "Flappy Bird" -> FlappyBirdActivity::class.java
            "Super Mystery Game" -> ActivityPlayersJournal::class.java
            else -> GamePortal::class.java
        })
        startActivity(intent)
    }

    private fun checkMysteryGameUnlockStatus() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val points = document.getLong("points") ?: 0
                        val isMysteryGameUnlocked = document.getBoolean("mysteryGameUnlocked") ?: false

                        if (points >= 500 && !isMysteryGameUnlocked) {
                            unlockMysteryGame(it.uid)
                        } else if (isMysteryGameUnlocked) {
                            updateMysteryGame()
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun unlockMysteryGame(userId: String) {
        Toast.makeText(this, "Congratulations! You've unlocked your first mystery game!", Toast.LENGTH_LONG).show()

        firestore.collection("users").document(userId)
            .update("mysteryGameUnlocked", true)
            .addOnSuccessListener {
                updateMysteryGame()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to unlock the mystery game: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateMysteryGame() {
        val mysteryGame = games.find { it.title == "???" }
        mysteryGame?.let {
            it.title = "Super Mystery Game"
            it.imageResId = R.drawable.dreadlord
            updateGameList()
        }
    }
}
