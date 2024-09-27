package com.example.practiceapplicationbrg.FloppyBird

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practiceapplicationbrg.R

class FloppyBird_GameHub : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_floppy_bird_game_hub)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the helpButton click listener
        findViewById<View>(R.id.helpButton).setOnClickListener {
            // Create an Intent to start the FloppyBirdHelpActivity
            val intent = Intent(this, FloppyBirdHelpActivity::class.java)
            startActivity(intent)
        }

        // Set up the playButton click listener
        findViewById<View>(R.id.playButton).setOnClickListener {
            // Create an Intent to start the FloppyBirdHelpActivity
            val intent = Intent(this, FloppyBird_MainActivity::class.java)
            startActivity(intent)
        }

    }
}
