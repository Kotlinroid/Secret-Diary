package com.shobhit.secretdiary.myActivities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shobhit.secretdiary.R
import com.shobhit.secretdiary.databinding.ActivityAuthBinding
import com.shobhit.secretdiary.myUtilities.showCustomSnackbar

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        val showSnackbar = intent.getBooleanExtra("showSnackbar", false)
        if (showSnackbar) {
            showCustomSnackbar(binding.root, "Logout Successful")
        }
    }
}