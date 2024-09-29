package com.example.practiceapplicationbrg

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.content.SharedPreferences
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.practiceapplicationbrg.ApiService
import com.example.practiceapplicationbrg.SubmitScoreRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroBrickBreakerActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout
    private lateinit var highScoreText: TextView

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var score = 0
    private var animator: ValueAnimator? = null

    private val brickRows = 9
    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4
    private var isBallLaunched = false
    private var lives = 3
    private var isGameOver = false

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var highScore: Int = 0
    private var currentUserUsername: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retro_brick_breaker)

        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)
        highScoreText = findViewById(R.id.highScoreText)

        val newgame = findViewById<Button>(R.id.newgame)

        newgame.setOnClickListener {
            initializeBricks()
            lives = 3 // Reset lives
            score = 0 // Reset score
            scoreText.text = "Score: $score"
            isGameOver = false // Reset isGameOver flag
            start()
            newgame.visibility = View.INVISIBLE
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("RetroBrickBreaker", MODE_PRIVATE)

        // Initialize API service
        setupRetrofit()

        // Fetch current user username
        fetchCurrentUserUsername {
            displayHighScore()
        }
    }

    private fun displayHighScore() {
        highScore = sharedPreferences.getInt("high_score", 0)
        findViewById<TextView>(R.id.highScoreText).text = "High Score: $highScore"
    }
    private fun updateHighScore(score: Int) {
        if (score > highScore) {
            with(sharedPreferences.edit()) {
                putInt("high_score", score)
                apply()
            }
            displayHighScore()
            submitScore(score)
        }
    }

    private fun submitScore(score: Int) {
        if (!currentUserUsername.isNullOrBlank()) {
            Log.d("RetroBrickBreakerActivity", "Submitting score: $score for user: $currentUserUsername")

            val scoreRequest = SubmitScoreRequest(
                gameName = "Retro Brick Breaker",
                username = currentUserUsername!!,
                score = score
            )

            Log.d("RetroBrickBreakerActivity", "Score Request: $scoreRequest")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.submitScore(scoreRequest)

                    if (response.isSuccessful) {
                        Log.d("RetroBrickBreakerActivity", "Score submitted successfully!")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RetroBrickBreakerActivity,
                                "Score submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.e("RetroBrickBreakerActivity", "Failed to submit score: ${response.errorBody()?.string()}")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RetroBrickBreakerActivity,
                                "Failed to submit score.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: Exception) {
                    Log.e("RetroBrickBreakerActivity", "Error submitting score", e)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RetroBrickBreakerActivity,
                            "Error submitting score.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Log.e("RetroBrickBreakerActivity", "Current user username is null, score submission skipped")
        }
    }

    private fun fetchCurrentUserUsername(onFetchComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("RetroBrickBreakerActivity", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("RetroBrickBreakerActivity", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RetroBrickBreakerActivity, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("RetroBrickBreakerActivity", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RetroBrickBreakerActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
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

    private fun initializeBricks() {
        // Clear existing bricks
        brickContainer.removeAllViews()

        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.drawable.ic_launcher_background)
                rowLayout.addView(brick)
            }
            brickContainer.addView(rowLayout)
        }
    }

    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }

    private fun movePaddle(x: Float) {
        paddleX = x - paddle.width / 2
        paddle.x = paddleX
    }

    private fun checkCollision() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + ball.width >= screenWidth) {
            ballSpeedX *= -1
        }

        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        if (ballY + ball.height >= paddle.y && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width) {
            ballSpeedY *= -1
            // Removed scoring here
        }

        // Check collision with bricks
        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowLayout.y
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom
                    ) {
                        brick.visibility = View.INVISIBLE
                        ballSpeedY *= -1
                        score++ // Only increment score by 1
                        scoreText.text = "Score: $score"
                    }
                }
            }
        }

        if (ballY + ball.height >= screenHeight) {
            lives--
            if (lives > 0) {
                Toast.makeText(this, "$lives balls left", Toast.LENGTH_SHORT).show()
                resetBallPosition()
                start()
            } else {
                if (!isGameOver) {
                    gameOver()
                    isGameOver = true
                }
            }
        }
    }

    private fun resetBallPosition() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels .toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = brickContainer.y + brickContainer.height - ball.height // Adjust the ballY position

        ball.x = ballX
        ball.y = ballY

        ballSpeedX = 0f
        ballSpeedY = 0f
    }

    private fun gameOver() {
        scoreText.text = "Game Over"
        findViewById<Button>(R.id.newgame).visibility = View.VISIBLE
        animator?.cancel() // Cancel the animation

        // Update user points
        updateHighScore(score)
    }

    private fun start() {
        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> movePaddle(event.rawX)
            }
            true
        }

        val screenDensity = resources.displayMetrics.density
        ballSpeedX = 2 * screenDensity // Reduce the initial ball speed
        ballSpeedY = -2 * screenDensity // Make the ball move upwards initially

        ballY = brickContainer.y + brickContainer.height - ball.height // Adjust the ballY position
        ball.x = ballX
        ball.y = ballY

        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.duration = Long.MAX_VALUE
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            moveBall()
            checkCollision()
        }
        animator?.start()
    }
}