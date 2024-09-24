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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GamePortal : AppCompatActivity() {

    private lateinit var gameAdapter: GameAdapter
    private lateinit var firestore: FirebaseFirestore
    private val games = mutableListOf(
        Game("Snake Eater", R.drawable.snake_eater),
        Game("Tic Tac Toe", R.drawable.tic_tac_toe),
        Game("Hang Man", R.drawable.hang_man),
        Game("Flappy Bird", R.drawable.flappy_bird),
        Game("???", R.drawable.mystery)
    )

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load user's favorites from Firestore
        loadFavoritesFromFirestore()
    }

    private fun loadFavoritesFromFirestore() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: continue
                    val game = games.find { it.title == title }
                    if (game != null) {
                        game.isFavorite = true // Mark as favorite
                    }
                }
                updateGameList() // Update the UI after loading favorites
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

    private fun updateGameList() {
        games.sortByDescending { it.isFavorite }
        gameAdapter.notifyDataSetChanged()
    }

    private fun saveFavoriteToFirestore(game: Game) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection("users").document(userId).collection("favorites").document(game.title)

        if (game.isFavorite) {
            favoriteRef.set(hashMapOf("title" to game.title, "imageResId" to game.imageResId))
        } else {
            favoriteRef.delete()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_daily_challenge -> {
                true
            }
            R.id.action_leader_board -> {
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
            // Add other game titles and their corresponding activities here
            else -> GamePortal::class.java
        })
        startActivity(intent)
    }
}
