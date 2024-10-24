package com.example.practiceapplicationbrg

import retrofit2.Call
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

    // Add a user to the Private Leaderboard
    @POST("leaderboard/private/{gameName}/{currentUser}/{selectedUser}")
    suspend fun addToPrivateLeaderboard(
        @Path("gameName") gameName: String,
        @Body request: AddToPrivateLeaderboardRequest
    ): Response<Unit>


    // Fetch private leaderboard for a specific game and user
    @GET("leaderboard/private/{gameName}/{username}")
    suspend fun getPrivateLeaderboard(
        @Path("gameName") gameName: String,
        @Path("username") username: String
    ): List<LeaderboardEntry>


    // Method to fetch all private games for a specific user
    @GET("leaderboard/private/privateGames/{username}")
    suspend fun getAllPrivateGames(@Path("username") username: String): List<String>


    // method to update high scores
    @POST("scores/scoress")
    suspend fun submitScore(@Body submitScoreRequest: SubmitScoreRequest): Response<Unit>

    // Method to fetch the high score for a specific game and user
    @GET("scores/highscore/{gameName}/{username}")
    suspend fun getHighScore(
        @Path("gameName") gameName: String,
        @Path("username") username: String
    ): HighScoreResponse

}


// Request body for adding a user to the private leaderboard
data class AddToPrivateLeaderboardRequest(
    val gameName: String,
    val currentUser: String,
    val selectedUser: String
)

// Request body for submitting a score
data class SubmitScoreRequest(
    val gameName: String,
    val username: String,
    val score: Int
)

// Response model for fetching the high score
data class HighScoreResponse(
    val highScore: Int
)