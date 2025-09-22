package com.shobhit.secretdiary.myUtilities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.shobhit.secretdiary.myDataClass.LoginResponse
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(loginResponse: LoginResponse, email: String) {
        prefs.edit {
            putString("ACCESS_TOKEN", loginResponse.access_token)
                .putString("USER_EMAIL", email)
                .putString("REFRESH_TOKEN", loginResponse.refresh_token)
                .putBoolean("IS_LOGGED_IN", true)
        }
    }

    fun getUserAccessToken(): String? {
        return prefs.getString("ACCESS_TOKEN", null)
    }

    fun getUserEmail(): String? {
        return prefs.getString("USER_EMAIL", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
        Log.d("SessionManager", "After logout: ${prefs.all}")
    }

}