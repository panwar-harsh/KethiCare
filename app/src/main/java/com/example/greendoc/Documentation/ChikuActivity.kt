package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityChikuBinding

class ChikuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChikuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChikuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}