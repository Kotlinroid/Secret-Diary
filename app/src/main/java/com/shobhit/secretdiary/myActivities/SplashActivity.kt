package com.shobhit.secretdiary.myActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.shobhit.secretdiary.databinding.ActivitySplashBinding
import com.shobhit.secretdiary.myUtilities.SessionManager

/**
 * Splash screen activity that appears when the app launches.
 * It checks whether the user is logged in or not and navigates
 * accordingly after a short delay.
 */
class SplashActivity : AppCompatActivity() {

    // ViewBinding object for accessing views from XML
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enables edge-to-edge display
        enableEdgeToEdge()

        // Delay for 2 seconds before navigating
        Handler(Looper.getMainLooper()).postDelayed({

            // Initialize SessionManager to check login status
            val sessionManager = SessionManager(this)

            if (sessionManager.isLoggedIn()) {
                // If user is logged in → navigate to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // If user is NOT logged in → navigate to AuthActivity
                startActivity(Intent(this, AuthActivity::class.java))
            }

            // Close SplashActivity so user cannot go back to it
            finish()

        }, 2000) // 2000ms = 2 seconds
    }
}
