package com.example.practiceapplicationbrg

import android.icu.util.LocaleData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "high_score_table")
class HighScoreDataClass(
    @ColumnInfo(name = "gameName") var gameName :String,
    @ColumnInfo(name = "username") var username :String,
    @ColumnInfo(name = "score") var score :Int,
    @ColumnInfo(name = "points") var points :Int,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)