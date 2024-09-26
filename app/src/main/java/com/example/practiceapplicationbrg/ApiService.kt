package com.example.practiceapplicationbrg

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("leaderboard/global/allGames")
    suspend fun getAllGames(): List<String>

    @GET("leaderboard/global/{gameName}")
    suspend fun getLeaderboard(@Path("gameName") gameName: String): List<LeaderboardEntry>
}
