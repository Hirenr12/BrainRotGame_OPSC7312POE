package com.example.practiceapplicationbrg
import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveLoginDetails(userId: String, email: String, isGoogleUser: Boolean) {
        val editor = prefs.edit()
        editor.putString("user_id", userId)
        editor.putString("email", email)
        editor.putBoolean("is_google_user", isGoogleUser)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains("user_id")
    }

    fun getUserDetails(): Map<String, String?> {
        return mapOf(
            "user_id" to prefs.getString("user_id", null),
            "email" to prefs.getString("email", null),
            "is_google_user" to prefs.getBoolean("is_google_user", false).toString()
        )
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}