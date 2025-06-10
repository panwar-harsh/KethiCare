package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityCoconutBinding

class CoconutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoconutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoconutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}