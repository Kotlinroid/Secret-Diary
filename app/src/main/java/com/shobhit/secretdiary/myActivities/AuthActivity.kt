package com.shobhit.secretdiary.myActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.shobhit.secretdiary.databinding.ActivityAuthBinding
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar

class AuthActivity : AppCompatActivity() {

    // DataBinding instance for accessing layout views
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** ---------------------- View Binding Setup ---------------------- **/
        binding = ActivityAuthBinding.inflate(layoutInflater)

        /** ---------------------- Disable Night Mode ---------------------- **/
        // Ensures the app always uses light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        /** ---------------------- Set Layout ---------------------- **/
        setContentView(binding.root)

        /** ---------------------- Show Snackbar on Logout ---------------------- **/
        val showSnackbar = intent.getBooleanExtra("showSnackbar", false)
        if (showSnackbar) {
            showCustomSnackbar(binding.root, "Logout Successful")
        }
    }
}
