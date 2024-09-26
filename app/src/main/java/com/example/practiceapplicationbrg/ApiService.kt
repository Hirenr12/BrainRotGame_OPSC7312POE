package com.example.practiceapplicationbrg

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("leaderboard/global/allGames")
    suspend fun getAllGames(): List<String>

    @GET("leaderboard/global/{gameName}")
    suspend fun getLeaderboard(@Path("gameName") gameName: String): List<LeaderboardEntry>

    // Updated API to add a user to the Private Leaderboard
    @POST("leaderboard/private/{gameName}/{currentUser}/{selectedUser}")
    suspend fun addToPrivateLeaderboard(
        @Path("gameName") gameName: String,
        @Body request: AddToPrivateLeaderboardRequest
    ): Response<Unit>

}


// Request body for adding a user to the private leaderboard
data class AddToPrivateLeaderboardRequest(
    val gameName: String,
    val currentUser: String,
    val selectedUser: String
)
