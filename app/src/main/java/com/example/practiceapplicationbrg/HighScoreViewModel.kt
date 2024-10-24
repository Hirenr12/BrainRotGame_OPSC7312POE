package com.example.practiceapplicationbrg

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class HighScoreViewModel(private val repository: HighScoreRepository) : ViewModel() {

    val highScores: LiveData<List<HighScoreDataClass>> = repository.getAllHighScores.asLiveData()

    fun addScore(newScore: HighScoreDataClass) = viewModelScope.launch {
        repository.insertHighScore(newScore)
    }

    fun updateScore(highScore: HighScoreDataClass) = viewModelScope.launch {
        repository.updateHighScore(highScore)
    }

    fun getHighestScore(username: String, gameName: String): LiveData<HighScoreDataClass?> {
        val result = MutableLiveData<HighScoreDataClass?>()
        viewModelScope.launch {
            result.value = repository.getHighestScore(username, gameName)
        }
        return result
    }

    fun addAllPoints(username: String, gameName: String): LiveData<Int?> {
        val result = MutableLiveData<Int?>()
        viewModelScope.launch {
            result.value = repository.addAllPoints(username, gameName)
        }
        return result
    }

    fun deleteAllScores() {
        viewModelScope.launch {
            repository.deleteAllScores()
        }
    }


    fun deleteAllScoresExceptHighest(gameName: String, username: String) {
        viewModelScope.launch {
            repository.deleteAllScoresExceptHighest(gameName,username)
        }
    }


}

class HighScoreModelFactory(private val repository: HighScoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HighScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HighScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
