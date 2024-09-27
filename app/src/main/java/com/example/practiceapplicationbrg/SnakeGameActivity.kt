package com.example.practiceapplicationbrg

import android.content.Context // Import added here
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.pm.ActivityInfo
import kotlinx.coroutines.*
import kotlin.math.abs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class SnakeGameActivity : AppCompatActivity() {
    private lateinit var canvas: CanvasView
    private lateinit var buttonUp: View
    private lateinit var buttonDown: View
    private lateinit var buttonLeft: View
    private lateinit var buttonRight: View
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var score: Int = 0 // Accumulated score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_snake_game)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize buttons
        buttonUp = findViewById(R.id.button_up)
        buttonDown = findViewById(R.id.button_down)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)

        // Set full-screen and portrait mode
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                        updatePoints() // Update points when the game ends
                        Snake.reset()
                    }

                    Snake.bodyParts.add(arrayOf(Snake.headX, Snake.headY))

                    // Check for food consumption
                    if (Snake.headX == Food.posX && Snake.headY == Food.posY) {
                        Food.generate()
                        score += 10 // Accumulate points locally
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
    }
    private fun updatePoints() {
        if (score > 0) {
            // Ensure that UI updates like Toast happen on the main thread
            runOnUiThread {
                // Show the toast with the correct score before resetting it
                Toast.makeText(this, "Game Over! Your score: $score", Toast.LENGTH_SHORT).show()

                // Update with accumulated score in Firebase first
                PointsManager.updateUserPoints(firestore, auth, score, this@SnakeGameActivity)

                // Now reset the score after the update and toast
                score = 0
            }
        }
    }


    // Swipe touch listener implementation
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