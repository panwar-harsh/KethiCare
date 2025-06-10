package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityBetelNutBinding

class BetelNutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBetelNutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBetelNutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}