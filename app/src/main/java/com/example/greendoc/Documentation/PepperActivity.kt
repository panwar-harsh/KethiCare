package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityPepperBinding

class PepperActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPepperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPepperBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}