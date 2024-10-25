package com.example.practiceapplicationbrg

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class HighScoreRepository(private val highScoreDAO: HighScoreDAO) {

    val getAllHighScores: Flow<List<HighScoreDataClass>> = highScoreDAO.getAllHighScores()

    @WorkerThread
    suspend fun insertHighScore(highScoreDC: HighScoreDataClass) {
        highScoreDAO.insertHighScore(highScoreDC)
    }

    @WorkerThread
    suspend fun updateHighScore(highScoreDC: HighScoreDataClass) {
        highScoreDAO.updateHighScore(highScoreDC)
    }

    @WorkerThread
    suspend fun getHighestScore(username: String, gameName: String): HighScoreDataClass? {
        return highScoreDAO.getHighestScore(username, gameName)
    }

    @WorkerThread
    suspend fun addAllPoints(username: String, gameName: String): Int? {
        return highScoreDAO.addAllPoints(username, gameName)
    }

    @WorkerThread
    suspend fun deleteAllScores() {
        highScoreDAO.deleteAllScores()
    }

    @WorkerThread
    suspend fun deleteAllScoresExceptHighest(gameName: String, username: String) {
        highScoreDAO.deleteAllScoresExceptHighest(gameName,username)
    }
}
