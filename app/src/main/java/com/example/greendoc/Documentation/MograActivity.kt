package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityMograBinding

class MograActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMograBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMograBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}