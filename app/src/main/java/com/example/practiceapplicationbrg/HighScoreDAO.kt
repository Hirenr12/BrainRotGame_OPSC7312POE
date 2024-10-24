package com.example.practiceapplicationbrg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDAO
{

    @Query("SELECT * FROM high_score_table ORDER BY score DESC")
    fun getAllHighScores(): Flow<List<HighScoreDataClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighScore(highScore: HighScoreDataClass)

    @Update
    suspend fun updateHighScore(highScore: HighScoreDataClass)

    @Query("DELETE FROM high_score_table")
    suspend fun deleteAllScores()

    // This method fetches the highest score for a specific user and game.
    @Query("SELECT * FROM high_score_table WHERE username = :username AND gameName = :gameName ORDER BY score DESC LIMIT 1")
    suspend fun getHighestScore(username: String, gameName: String): HighScoreDataClass?

    @Query("SELECT SUM(points) FROM high_score_table WHERE username = :username AND gameName = :gameName")
    suspend fun addAllPoints(username: String, gameName: String): Int?

    // New method to delete all scores except the highest for a specific game
    @Query("""
        DELETE FROM high_score_table
        WHERE gameName = :gameName AND username = :username
        AND score NOT IN (
            SELECT MAX(score) FROM high_score_table WHERE gameName = :gameName
        )
    """)
    suspend fun deleteAllScoresExceptHighest(gameName: String, username: String)


}