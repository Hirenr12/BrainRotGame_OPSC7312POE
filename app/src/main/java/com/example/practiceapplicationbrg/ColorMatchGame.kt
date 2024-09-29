package com.example.practiceapplicationbrg

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
import kotlin.random.Random

class ColorMatchGame : AppCompatActivity() {
    private lateinit var colorDisplay: TextView
    private lateinit var scoreText: TextView
    private lateinit var timeText: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button

    private var currentUserUsername: String? = null
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

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

        trueButton.setOnClickListener { checkAnswerAsync(true) }
        falseButton.setOnClickListener { checkAnswerAsync(false) }

        startGame()
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
                // This block will be executed if the coroutine is cancelled
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
        lifecycleScope.launch(Dispatchers.Main) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            colorDisplay.text = "Game Over!"

            fetchCurrentUserUsername {
                // Check for the score passed from PlayGameActivity after fetching the username
                val score = intent.getIntExtra("score", 0)
                if (score > 0) {
                    // Submit the score here

                    updatePoints(score) // Update points

                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        gameJob?.cancel()
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
                // Call the callback function when fetching is complete
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
            }
        }
    }

    private fun updatePoints(score: Int) {
        if (score > 0) {
            // Ensure that UI updates like Toast happen on the main thread
            runOnUiThread {
                // Show the toast with the correct score
                Toast.makeText(this, "You Got: $score Points!", Toast.LENGTH_SHORT).show()
            }

            // Update with accumulated score in Firebase
            PointsManager.updateUserPoints(db, auth, score, this@ColorMatchGame)
        }
    }
}