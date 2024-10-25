package com.example.practiceapplicationbrg

import android.app.Application

class TodoApplication: Application()
{
    private val database by lazy {HighScoreDatabase.getDatabase(this)}
    val repository by lazy {HighScoreRepository(database.highScoreDAO())}
}