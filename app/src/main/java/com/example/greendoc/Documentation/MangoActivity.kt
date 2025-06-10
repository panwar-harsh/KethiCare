package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityMangoBinding

class MangoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMangoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}