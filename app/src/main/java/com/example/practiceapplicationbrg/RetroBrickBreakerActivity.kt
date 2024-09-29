package com.example.practiceapplicationbrg

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
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

class RetroBrickBreakerActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retro_brick_breaker)

        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)

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
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

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
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        PointsManager.updateUserPoints(firestore, auth, score, this)
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