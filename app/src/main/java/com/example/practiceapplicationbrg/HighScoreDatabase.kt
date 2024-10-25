package com.example.practiceapplicationbrg

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HighScoreDataClass::class], version = 1, exportSchema = false)
abstract class HighScoreDatabase : RoomDatabase()
{
    abstract fun highScoreDAO(): HighScoreDAO

    companion object
    {
        @Volatile
        private var INSTANCE: HighScoreDatabase? = null

        fun getDatabase(context: Context) : HighScoreDatabase
        {
            return INSTANCE?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HighScoreDatabase::class.java,
                    "high_score_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }




}