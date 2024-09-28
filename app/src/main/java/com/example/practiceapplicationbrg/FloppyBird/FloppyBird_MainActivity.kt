package com.example.practiceapplicationbrg.FloppyBird

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practiceapplicationbrg.ApiService
import com.example.practiceapplicationbrg.PointsManager
import com.example.practiceapplicationbrg.R
import com.example.practiceapplicationbrg.SubmitScoreRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FloppyBird_MainActivity : AppCompatActivity() {

    private val Tag = "FloppyBirdMainActivity"

    private lateinit var btnPlay: ImageButton
    private lateinit var tvHighScore: TextView
    private lateinit var tvBestTime: TextView
    private lateinit var btnClear: Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var apiService: ApiService

    private var currentUserUsername: String? = null
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_floppy_bird_main)
        ScreenSize.getScreenSize(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("PracticeApplicationBRG", MODE_PRIVATE)

        btnPlay = findViewById(R.id.btnPlay)
        tvHighScore = findViewById(R.id.tvHighScore)
        tvBestTime = findViewById(R.id.tvBestTime)
        btnClear = findViewById(R.id.btnClear)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupRetrofit()
        fetchCurrentUserUsername {
            // Check for the score passed from PlayGameActivity after fetching the username
            val score = intent.getIntExtra("score", 0)
            if (score > 0) {
                updateHighScore(score)  // Update high score
                submitScore(score)      // Submit the score here

                updatePoints(score) // Update points

            }
        }

        btnPlay.setOnClickListener {
            val iPlayGame = Intent(this@FloppyBird_MainActivity, PlayGameActivity::class.java)
            startActivity(iPlayGame)
            finish()
            Log.d(Tag, "Button Play Clicked")
        }

        btnClear.setOnClickListener {
            clearScores()
        }

        // Display the high score and best time
        displayHighScore()
        displayBestTime()
    }

    private fun displayHighScore() {
        val highScore = sharedPreferences.getInt("high_score", 0)
        tvHighScore.text = "High Score: $highScore"
    }

    private fun displayBestTime() {
        val bestTimeMillis = sharedPreferences.getLong("best_time", Long.MAX_VALUE)
        if (bestTimeMillis != Long.MAX_VALUE) {
            val minutes = (bestTimeMillis / 60000).toInt()
            val seconds = ((bestTimeMillis % 60000) / 1000).toInt()
            tvBestTime.text = String.format("Best Time: %d:%02d", minutes, seconds)
        } else {
            tvBestTime.text = "Best Time: --:--"
        }
    }

    private fun updateHighScore(score: Int) {
        val highScore = sharedPreferences.getInt("high_score", 0)
        if (score > highScore) {
            with(sharedPreferences.edit()) {
                putInt("high_score", score)
                apply()
            }
            displayHighScore()
        }
    }

    private fun clearScores() {
        with(sharedPreferences.edit()) {
            remove("high_score")
            remove("best_time")
            apply()
        }
        // Update UI to reflect cleared scores
        displayHighScore()
        displayBestTime()
    }



    private fun submitScore(score: Int) {
        // Ensure the currentUserUsername is not null or blank
        if (!currentUserUsername.isNullOrBlank()) {
            Log.d(Tag, "Submitting score: $score for user: $currentUserUsername")

            // Create the score request
            val scoreRequest = SubmitScoreRequest(
                gameName = "Floppy Bird",
                username = currentUserUsername!!,
                score = score // Ensure this matches the data class
            )

            Log.d(Tag, "Score Request: $scoreRequest") // Log the full request

            // Use a CoroutineScope for launching the coroutine
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Send the score request to the API
                    val response = apiService.submitScore(scoreRequest)

                    // Check if the response is successful
                    if (response.isSuccessful) {
                        Log.d(Tag, "Score submitted successfully!")

                        // Show success toast message
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FloppyBird_MainActivity, // Replace with your actual activity name
                                "Score submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.e(Tag, "Failed to submit score: ${response.errorBody()?.string()}")

                        // Optionally, show failure toast message
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FloppyBird_MainActivity, // Replace with your actual activity name
                                "Failed to submit score.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: Exception) {
                    Log.e(Tag, "Error submitting score", e)

                    // Optionally, show error toast message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@FloppyBird_MainActivity, // Replace with your actual activity name
                            "Error submitting score.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Log.e(Tag, "Current user username is null, score submission skipped")
        }
    }








    private fun fetchCurrentUserUsername(onFetchComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("FloppyBird_MainActivity", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("FloppyBird_MainActivity", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FloppyBird_MainActivity, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FloppyBird_MainActivity", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FloppyBird_MainActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Call the callback function when fetching is complete
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
            }
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }









    private fun updatePoints(score: Int) {
        if (score > 0) {
            // Ensure that UI updates like Toast happen on the main thread
            runOnUiThread {
                // Show the toast with the correct score
                Toast.makeText(this, "You Got: $score Points!", Toast.LENGTH_SHORT).show()
            }

            // Update with accumulated score in Firebase
            PointsManager.updateUserPoints(db, auth, score, this@FloppyBird_MainActivity)
        }
    }




}
