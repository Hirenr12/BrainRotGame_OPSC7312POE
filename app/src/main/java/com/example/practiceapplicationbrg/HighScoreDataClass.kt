package com.example.practiceapplicationbrg

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "high_scores")
data class HighScoreDataClass(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameName: String,
    val username: String,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)
