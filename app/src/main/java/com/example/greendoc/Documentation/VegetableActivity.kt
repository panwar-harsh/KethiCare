package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityVegetableBinding

class VegetableActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVegetableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVegetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}