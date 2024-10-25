package com.example.practiceapplicationbrg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executor
import androidx.core.content.ContextCompat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import org.chromium.net.NetworkChangeNotifier.isOnline


class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Biometric Authentication setup
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (UserSessionManager(this@Login).isLoggedIn()) {
                        // Perform biometric login regardless of network state
                        Toast.makeText(this@Login, "Biometric login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Login, GamePortal::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Login failed: No cached login found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for GameHub")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        // Set up biometric authentication if available
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        }

        // Set up edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Ensure you have this in strings.xml
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Get references to the input fields, buttons, and the TextView
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val submitButton: Button = findViewById(R.id.submit_button)
        val googleSignInButton: ImageView = findViewById(R.id.ic_google)
        val txtSignInGoogle: TextView = findViewById(R.id.txtSignInGoogle)  // Reference to the TextView

        // Set up the submit button click listener
        submitButton.setOnClickListener {
            loginUser(emailField.text.toString(), passwordField.text.toString())
        }

        // Set up Google sign-in button and TextView click listeners
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
        txtSignInGoogle.setOnClickListener {  // Make the TextView clickable
            signInWithGoogle()
        }
    }

    private fun loginUser(email: String, password: String) {
        if (isOnline()) {
            // Online mode: Authenticate using Firebase
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Cache login details for offline use
                        val userId = auth.currentUser?.uid ?: ""
                        UserSessionManager(this).saveLoginDetails(userId, email, false)

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, GamePortal::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            // Offline mode: Use cached credentials if available
            if (UserSessionManager(this).isLoggedIn()) {
                Toast.makeText(this, "Offline login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, GamePortal::class.java))
                finish()
            } else {
                Toast.makeText(this, "No internet and no cached login found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            account?.let {
                if (isOnline()) {
                    val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Cache Google login details
                                val userId = auth.currentUser?.uid ?: ""
                                UserSessionManager(this).saveLoginDetails(userId, it.email ?: "", true)

                                Toast.makeText(this, "Google login successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, GamePortal::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Google login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else if (UserSessionManager(this).isLoggedIn()) {
                    // Offline login
                    Toast.makeText(this, "Offline Google login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GamePortal::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "No cached Google login available", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Function to check if device is online
    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    override fun onStart() {
        super.onStart()
        // Notify the user about network state at startup
        if (!isOnline()) {
            Toast.makeText(this, "You are offline. Offline mode enabled.", Toast.LENGTH_SHORT).show()
        }
    }
}