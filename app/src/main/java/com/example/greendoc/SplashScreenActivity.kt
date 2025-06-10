package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        val lineDraw = AnimationUtils.loadAnimation(this, R.anim.line_draw)

        // Animate app name
        binding.appName.visibility = View.VISIBLE
        binding.appName.startAnimation(fadeIn)

        // Animate app icon after app name animation
        Handler(Looper.getMainLooper()).postDelayed({
            binding.appIcon.visibility = View.VISIBLE
            binding.appIcon.startAnimation(slideInLeft)
        }, 1000)

        // Animate bottom line after app icon animation
        Handler(Looper.getMainLooper()).postDelayed({
            binding.bottomLine.visibility = View.VISIBLE
            binding.bottomLine.startAnimation(lineDraw)
        }, 2000)

        // Navigate to StartActivity after all animations
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }, 4000)
    }
}