package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityRiceBinding

class RiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}