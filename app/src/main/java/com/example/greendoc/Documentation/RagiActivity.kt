package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityRagiBinding

class RagiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRagiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRagiBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}