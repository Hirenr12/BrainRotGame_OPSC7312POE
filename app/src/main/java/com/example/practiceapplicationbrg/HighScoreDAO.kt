package com.example.practiceapplicationbrg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighScoreDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: HighScoreDataClass)

    @Query("SELECT * FROM high_scores WHERE username = :username AND gameName = :gameName ORDER BY score DESC LIMIT 1")
    suspend fun getHighScore(username: String, gameName: String): HighScoreDataClass?

    @Query("SELECT * FROM high_scores")
    suspend fun getAllHighScores(): List<HighScoreDataClass>

    @Query("DELETE FROM high_scores")
    suspend fun clearAllScores()
}