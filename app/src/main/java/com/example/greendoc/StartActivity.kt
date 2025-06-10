package com.example.greendoc

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.greendoc.Ecommerce.AuthActivity
import com.example.greendoc.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Check if the user is already logged in
        val user = FirebaseAuth.getInstance().currentUser
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        if (user != null || !isFirstTime) {
            // If user is logged in or app is already opened before, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // If first time, continue with StartActivity
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Mark that the app has been opened once
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
