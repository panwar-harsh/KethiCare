package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityCinnamonBinding

class CinnamonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCinnamonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCinnamonBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}