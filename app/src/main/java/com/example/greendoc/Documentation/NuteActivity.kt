package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityNuteBinding

class NuteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNuteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}