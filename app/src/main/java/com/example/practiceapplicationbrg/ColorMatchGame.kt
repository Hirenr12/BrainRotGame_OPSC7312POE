package com.example.practiceapplicationbrg

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class ColorMatchGame : AppCompatActivity() {
    private lateinit var colorDisplay: TextView
    private lateinit var scoreText: TextView
    private lateinit var timeText: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUserUsername: String? = null

    private var score = 0
    private var timeLeft = 10 // Game duration in seconds
    private val colors = listOf("RED", "BLUE", "GREEN", "YELLOW", "PURPLE", "ORANGE")

    private var gameJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_match)

        colorDisplay = findViewById(R.id.colorDisplay)
        scoreText = findViewById(R.id.scoreText)
        timeText = findViewById(R.id.timeText)
        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)

        // Setup Retrofit
        setupRetrofit()

        // Fetch current user username
        fetchCurrentUserUsername {
            startGame()
        }

        trueButton.setOnClickListener { checkAnswerAsync(true) }
        falseButton.setOnClickListener { checkAnswerAsync(false) }
    }

    private fun startGame() {
        score = 0
        timeLeft = 10
        updateScore()
        updateTimer()

        gameJob = lifecycleScope.launch {
            try {
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft--
                    updateTimer()
                }
                endGame()
            } finally {
                endGame()
            }
        }

        nextColorAsync()
    }

    private fun nextColorAsync() {
        lifecycleScope.launch(Dispatchers.Default) {
            val randomColor = colors[Random.nextInt(colors.size)]
            val randomTextColor = colors[Random.nextInt(colors.size)]
            withContext(Dispatchers.Main) {
                colorDisplay.text = randomColor
                colorDisplay.setTextColor(getColorFromString(randomTextColor))
            }
        }
    }

    private fun checkAnswerAsync(userAnswer: Boolean) {
        lifecycleScope.launch(Dispatchers.Default) {
            val colorName = colorDisplay.text.toString()
            val textColor = getColorName(colorDisplay.currentTextColor)
            val correctAnswer = colorName == textColor

            if (userAnswer == correctAnswer) {
                score++
            } else {
                score = maxOf(0, score - 1)  // Prevent negative score
            }

            withContext(Dispatchers.Main) {
                updateScore()
                nextColorAsync()
            }
        }
    }

    private fun getColorFromString(colorString: String): Int {
        return when (colorString) {
            "RED" -> Color.RED
            "BLUE" -> Color.BLUE
            "GREEN" -> Color.GREEN
            "YELLOW" -> Color.YELLOW
            "PURPLE" -> Color.rgb(128, 0, 128)
            "ORANGE" -> Color.rgb(255, 165, 0)
            else -> Color.BLACK
        }
    }

    private fun getColorName(color: Int): String {
        return when (color) {
            Color.RED -> "RED"
            Color.BLUE -> "BLUE"
            Color.GREEN -> "GREEN"
            Color.YELLOW -> "YELLOW"
            Color.rgb(128, 0, 128) -> "PURPLE"
            Color.rgb(255, 165, 0) -> "ORANGE"
            else -> "UNKNOWN"
        }
    }

    private fun updateScore() {
        scoreText.text = "Score: $score"
    }

    private fun updateTimer() {
        timeText.text = "Time: $timeLeft"
    }

    private fun endGame() {
        gameJob?.cancel()
        lifecycleScope.launch(Dispatchers.Main) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            colorDisplay.text = "Game Over!"

            // Update high score and submit score
            updatePoints()
        }
    }

    private fun displayHighScore() {
        val highScore = sharedPreferences.getInt("high_score", 0)
        Toast.makeText(this, "High Score: $highScore", Toast.LENGTH_SHORT).show()
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

    private fun updatePoints() {
        if (score > 0) {
            Toast.makeText(this, "Game Over! Your score: $score", Toast.LENGTH_SHORT).show()
            updateHighScore(score)
            submitScore(score)
            score = 0
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://brainrotapi.ue.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun submitScore(score: Int) {
        if (!currentUserUsername.isNullOrBlank()) {
            Log.d("ColorMatchGame", "Submitting score: $score for user: $currentUserUsername")

            val scoreRequest = SubmitScoreRequest(
                gameName = "Color Match Game",
                username = currentUserUsername!!,
                score = score
            )

            Log.d("ColorMatchGame", "Score Request: $scoreRequest")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.submitScore(scoreRequest)

                    if (response.isSuccessful) {
                        Log.d("ColorMatchGame", "Score submitted successfully!")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ColorMatchGame,
                                "Score submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.e("ColorMatchGame", "Failed to submit score: ${response.errorBody()?.string()}")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ColorMatchGame,
                                "Failed to submit score.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: Exception) {
                    Log.e("ColorMatchGame", "Error submitting score", e)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ColorMatchGame,
                            "Error submitting score.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Log.e("ColorMatchGame", "Current user username is null, score submission skipped")
        }
    }

    private fun fetchCurrentUserUsername(onFetchComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("ColorMatchGame", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("ColorMatchGame", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ColorMatchGame, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ColorMatchGame", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ColorMatchGame, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameJob?.cancel()
    }
}
