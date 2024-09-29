package com.example.practiceapplicationbrg

// SnakeGameActivity.kt

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practiceapplicationbrg.ApiService
import com.example.practiceapplicationbrg.SubmitScoreRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class SnakeGameActivity : AppCompatActivity() {

    private lateinit var canvas: CanvasView
    private lateinit var buttonUp: View
    private lateinit var buttonDown: View
    private lateinit var buttonLeft: View
    private lateinit var buttonRight: View
    private lateinit var tvHighScore: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var score: Int = 0
    private var currentUserUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_snake_game)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize buttons
        buttonUp = findViewById(R.id.button_up)
        buttonDown = findViewById(R.id.button_down)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)

        // Initialize high score text view
        tvHighScore = findViewById(R.id.tvHighScore)

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("SnakeGame", MODE_PRIVATE)

        // Set full-screen and portrait mode
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()

        // Touch control
        canvas = findViewById(R.id.canvas)

        canvas.setOnTouchListener(object : OnSwipeTouchListener(this) {
            override fun onSwipeLeft() {
                Snake.alive = true
                if (Snake.direction != "right")
                    Snake.direction = "left"
            }

            override fun onSwipeRight() {
                Snake.alive = true
                if (Snake.direction != "left")
                    Snake.direction = "right"
            }

            override fun onSwipeTop() {
                Snake.alive = true
                if (Snake.direction != "down")
                    Snake.direction = "up"
            }

            override fun onSwipeBottom() {
                Snake.alive = true
                if (Snake.direction != "up")
                    Snake.direction = "down"
            }
        })

        // Move the snake
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                while (Snake.alive) {
                    when (Snake.direction) {
                        "up" -> {
                            Snake.headY -= 50
                        }
                        "down" -> {
                            Snake.headY += 50
                        }
                        "left" -> {
                            Snake.headX -= 50
                        }
                        "right" -> {
                            Snake.headX += 50
                        }
                    }
                    if (!Snake.possibleMove()) {
                        Snake.alive = false
                        updatePoints()
                        Snake.reset()
                    }

                    Snake.bodyParts.add(arrayOf(Snake.headX, Snake.headY))

                    // Check for food consumption
                    if (Snake.headX == Food.posX && Snake.headY == Food.posY) {
                        Food.generate()
                        score += 10
                    } else {
                        Snake.bodyParts.removeAt(0)
                    }

                    canvas.invalidate()
                    delay(150)
                }
            }
        }

        // Button controls
        buttonUp.setOnClickListener {
            Snake.alive = true
            if (Snake.direction != "down")
                Snake.direction = "up"
        }
        buttonDown.setOnClickListener {
            Snake.alive = true
            if (Snake.direction != "up")
                Snake.direction = "down"
        }
        buttonLeft.setOnClickListener {
            Snake.alive = true
            if (Snake.direction != "right")
                Snake.direction = "left"
        }
        buttonRight.setOnClickListener {
            Snake.alive = true
            if (Snake.direction != "left")
                Snake.direction = "right"
        }

        setupRetrofit()
        fetchCurrentUserUsername {
            displayHighScore()
        }
    }

    private fun displayHighScore() {
        val highScore = sharedPreferences.getInt("high_score", 0)
        tvHighScore.text = "High Score: $highScore"
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
            runOnUiThread {
                Toast.makeText(this, "Game Over! Your score: $score", Toast.LENGTH_SHORT).show()
                updateHighScore(score)
                submitScore(score)
                score = 0
            }
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
            Log.d("SnakeGameActivity", "Submitting score: $score for user: $currentUserUsername")

            val scoreRequest = SubmitScoreRequest(
                gameName = "Snake Game",
                username = currentUserUsername!!,
                score = score
            )

            Log.d("SnakeGameActivity", "Score Request: $scoreRequest")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.submitScore(scoreRequest)

                    if (response.isSuccessful) {
                        Log.d("SnakeGameActivity", "Score submitted successfully!")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SnakeGameActivity,
                                "Score submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.e("SnakeGameActivity", "Failed to submit score: ${response.errorBody()?.string()}")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SnakeGameActivity,
                                "Failed to submit score.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: Exception) {
                    Log.e("SnakeGameActivity", "Error submitting score", e)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SnakeGameActivity,
                            "Error submitting score.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Log.e("SnakeGameActivity", "Current user username is null, score submission skipped")
        }
    }

    private fun fetchCurrentUserUsername(onFetchComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("SnakeGameActivity", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("SnakeGameActivity", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SnakeGameActivity, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("SnakeGameActivity", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SnakeGameActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
            }
        }
    }

    open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {
        private val gestureDetector = GestureDetector(context, GestureListener())

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                try {
                    if (e1 != null) {
                        val diffY = e2.y - e1.y
                        val diffX = e2.x - e1.x
                        if (abs(diffX) > abs(diffY)) {
                            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) onSwipeRight() else onSwipeLeft()
                            }
                        } else {
                            if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffY > 0) onSwipeBottom() else onSwipeTop()
                            }
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                return false
            }
        }

        open fun onSwipeRight() {}
        open fun onSwipeLeft() {}
        open fun onSwipeTop() {}
        open fun onSwipeBottom() {}
    }
}