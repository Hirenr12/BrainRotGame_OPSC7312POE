package com.example.practiceapplicationbrg.FloppyBird

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practiceapplicationbrg.R
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import android.widget.ImageButton

class FloppyBird_MainActivity : AppCompatActivity() {

    private val Tag = "FloppyBirdMainActivity"

    private lateinit var btnPlay: ImageButton
    private lateinit var tvHighScore: TextView
    private lateinit var tvBestTime: TextView
    private lateinit var btnClear: Button
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Check for the score passed from PlayGameActivity
        val score = intent.getIntExtra("score", 0)
        if (score > 0) {
            updateHighScore(score)
        }
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



}