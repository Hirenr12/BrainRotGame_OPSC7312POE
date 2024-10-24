package com.example.practiceapplicationbrg.FloppyBird

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.practiceapplicationbrg.ApiService
import com.example.practiceapplicationbrg.HighScoreDataClass
import com.example.practiceapplicationbrg.HighScoreModelFactory
import com.example.practiceapplicationbrg.HighScoreViewModel
import com.example.practiceapplicationbrg.NetworkChangeReceiver
import com.example.practiceapplicationbrg.NetworkUtil
import com.example.practiceapplicationbrg.PointsManager
import com.example.practiceapplicationbrg.R
import com.example.practiceapplicationbrg.SubmitScoreRequest
import com.example.practiceapplicationbrg.TodoApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

class FloppyBird_MainActivity : AppCompatActivity() {

    private val Tag = "FloppyBirdMainActivity"

    private lateinit var btnPlay: ImageButton
    private lateinit var tvHighScore: TextView
    private lateinit var tvBestTime: TextView
    private lateinit var btnClear: Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var apiService: ApiService

    private var currentUserUsername: String? = null
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    // Handler for periodically checking network status
    private val handler = Handler(Looper.getMainLooper())
    private val networkCheckInterval: Long = 3000 // 3 seconds

    // Variable to track the previous network status
    private var wasOnline: Boolean? = null

    // Runnable task to check network status
    private val networkStatusChecker = object : Runnable {
        override fun run() {
            checkNetworkStatus()
            // Re-run the check after the specified interval
            handler.postDelayed(this, networkCheckInterval)
        }
    }

    private lateinit var highScoreViewModel: HighScoreViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_floppy_bird_main)
        ScreenSize.getScreenSize(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("PracticeApplicationBRG", MODE_PRIVATE)

        btnPlay = findViewById(R.id.btnPlay)
        tvHighScore = findViewById(R.id.tvHighScore)
        tvBestTime = findViewById(R.id.tvBestTime)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupRetrofit()
        fetchCurrentUserUsername {

            displayHighScore()

            fetchHighScoreDB()

            // Check for the score passed from PlayGameActivity after fetching the username
            val score = intent.getIntExtra("score", 0)
            if (score > 0) {
                submitScore(score)      // Submit the score here
                updatePoints(score) // Update points

            }
        }


        // Initialize ViewModel
        val application = application as TodoApplication
        val highScoreFactory = HighScoreModelFactory(application.repository)
        highScoreViewModel = ViewModelProvider(this, highScoreFactory).get(HighScoreViewModel::class.java)


        btnPlay.setOnClickListener {
            val iPlayGame = Intent(this@FloppyBird_MainActivity, PlayGameActivity::class.java)
            startActivity(iPlayGame)
            finish()
            Log.d(Tag, "Button Play Clicked")
        }

        displayBestTime()


        // Start checking network status periodically
        handler.post(networkStatusChecker)
    }















    override fun onDestroy() {
        super.onDestroy()
        // Stop the network status checker when the activity is destroyed
        handler.removeCallbacks(networkStatusChecker)
    }



    private fun checkNetworkStatus() {
        // Check if the user is connected to the internet
        val isOnline = NetworkUtil.isConnected(this)

        // Only show a Toast if the network status has changed
        if (wasOnline == null || wasOnline != isOnline) {
            if (isOnline) {
                Toast.makeText(this, "You are online!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "You are offline. Your scores and points will be stored locally.", Toast.LENGTH_SHORT).show()
            }
            // Update the previous status
            wasOnline = isOnline
        }
    }

    private fun displayHighScore() {
        Log.d(Tag, "Initiating displayHighScore")
        fetchHighScoreApi { highScore ->
            Log.d(Tag, "High Score fetched: $highScore")
            tvHighScore.text = "High Score: $highScore"
            Log.d(Tag, "High Score displayed successfully")
        }
    }

    private fun displayBestTime() {
        val bestTimeMillis = sharedPreferences.getLong("best_time", Long.MAX_VALUE)
        if (bestTimeMillis != Long.MAX_VALUE) {
            val minutes = (bestTimeMillis / 60000).toInt()
            val seconds = ((bestTimeMillis % 60000) / 1000).toInt()
            tvBestTime.text = String.format("Best Time: %d:%02d", minutes, seconds)
        } else {
            tvBestTime.text = "Best Time: --:--"
        }
    }





    /*
    private fun updateHighScore(score: Int) {
        val highScore = sharedPreferences.getInt("high_score", 0)
        if (score > highScore) {
            with(sharedPreferences.edit()) {
                putInt("high_score", score)
                apply()
            }
            displayHighScore()
        }
    }
     */





    private fun submitScore(score: Int) {
        if (!currentUserUsername.isNullOrBlank()) {
            Log.d(Tag, "Submitting score: $score for user: $currentUserUsername")

            // Check network status
            val isOnline = NetworkUtil.isConnected(this)

            if (isOnline) {
                // Online: submit score to API
                val scoreRequest = SubmitScoreRequest(
                    gameName = "Floppy Bird",
                    username = currentUserUsername!!,
                    score = score
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.submitScore(scoreRequest)

                        if (response.isSuccessful) {
                            Log.d(Tag, "Score submitted successfully!")

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@FloppyBird_MainActivity,
                                    "Score submitted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.e(Tag, "Failed to submit score: ${response.errorBody()?.string()}")

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@FloppyBird_MainActivity,
                                    "Failed to submit score.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(Tag, "Error submitting score", e)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FloppyBird_MainActivity,
                                "Error submitting score.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                // Offline: store score locally
                storeScoreLocally(
                    gameName = "Floppy Bird",
                    username = currentUserUsername!!,
                    score = score,
                    points = score
                )
            }
        } else {
            Log.e(Tag, "Current user username is null, score submission skipped")
        }
    }











    private fun fetchCurrentUserUsername(onFetchComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserUid = auth.currentUser?.uid ?: return@launch

            try {
                val documentSnapshot = db.collection("users").document(currentUserUid).get().await()
                if (documentSnapshot.exists()) {
                    currentUserUsername = documentSnapshot.getString("username")
                    Log.d("FloppyBird_MainActivity", "Fetched username: $currentUserUsername")
                } else {
                    Log.e("FloppyBird_MainActivity", "User document not found")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@FloppyBird_MainActivity, "Failed to fetch user information", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FloppyBird_MainActivity", "Error fetching username", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FloppyBird_MainActivity, "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Call the callback function when fetching is complete
                withContext(Dispatchers.Main) {
                    onFetchComplete()
                }
            }
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://brainrotapi.ue.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun updatePoints(score: Int) {
        if (score > 0) {
            // Ensure that UI updates like Toast happen on the main thread
            runOnUiThread {
                // Show the toast with the correct score
                Toast.makeText(this, "You Got: $score Points!", Toast.LENGTH_SHORT).show()
            }

            // Update with accumulated score in Firebase
            PointsManager.updateUserPoints(db, auth, score, this@FloppyBird_MainActivity)
        }
    }

    private fun fetchHighScoreApi(callback: (Int) -> Unit) {
        if (!currentUserUsername.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Encode the game name to handle spaces
                    val gameName = "Floppy Bird"
                    Log.d(Tag, "Game Name: $gameName")

                    val response = apiService.getHighScore(gameName, currentUserUsername!!)
                    Log.d(Tag, "API call successful, high score fetched: ${response.highScore}")

                    withContext(Dispatchers.Main) {
                        callback(response.highScore)
                    }
                } catch (e: retrofit2.HttpException) {
                    Log.e(Tag, "HTTP Error: ${e.code()} - ${e.message()}", e)
                    withContext(Dispatchers.Main) {
                        callback(0)
                    }
                } catch (e: Exception) {
                    Log.e(Tag, "Exception occurred while fetching high score", e)
                    withContext(Dispatchers.Main) {
                        callback(0)
                    }
                }
            }
        } else {
            Log.e(Tag, "Current user username is null, high score fetch skipped")
            callback(0)
        }
    }







    private fun storeScoreLocally(gameName: String, username: String, score: Int, points: Int) {
        Log.d(Tag, "Storing score locally for offline mode")

        // Create a HighScoreDataClass object
        val highScore = HighScoreDataClass(
            gameName = gameName,
            username = username,
            score = score,
            points = points
        )

        // Save the score using the ViewModel
        highScoreViewModel.addScore(highScore)

        // Notify user
        Toast.makeText(
            this,
            "You are offline. Score saved locally.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun fetchHighScoreDB() {
        // Ensure the current user's username is not null
        val username = currentUserUsername ?: return
        val gameName = "Floppy Bird"

        // Call the ViewModel's method to get the high score from the local database
        val highScoreLiveData = highScoreViewModel.getHighestScore(username, gameName)

        // Observe the LiveData to handle the result when it changes
        highScoreLiveData.observe(this, { highScoreData ->
            highScoreData?.let {
                // Fetch the high score from the API
                fetchHighScoreApi { apiHighScore ->
                    // If the local high score is higher than the one from the API, submit it
                    if (it.score > apiHighScore) {
                        submitScore(it.score)
                        fetchAllPointsDB(username,gameName)
                    }
                }
            } ?: run {
                // Handle the case when there's no high score in the local database
                Log.d("FloppyBird_MainActivity", "No high score found in the local database.")
            }
        })
    }


    private fun fetchAllPointsDB(username: String, gameName: String) {
        // Call the ViewModel's method to get the total points from the local database
        val totalPointsLiveData = highScoreViewModel.addAllPoints(username, gameName)

        // Observe the LiveData to handle the result when it changes
        totalPointsLiveData.observe(this, { totalPoints ->
            totalPoints?.let {
                // Display or use the total points as needed
                Log.d("FloppyBird_MainActivity", "Total Points for $gameName by $username: $it")
                Toast.makeText(
                    this,
                    "Total Points for $gameName by $username: $it",
                    Toast.LENGTH_SHORT
                ).show()
                updatePoints(totalPoints)
            } ?: run {
                // Handle the case when there are no points found
                Log.d("FloppyBird_MainActivity", "No points found in the local database.")
                Toast.makeText(
                    this,
                    "No points found for $gameName by $username",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }










}
