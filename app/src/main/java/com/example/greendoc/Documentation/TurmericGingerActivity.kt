package com.example.greendoc.Documentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityTurmericGingerBinding

class TurmericGingerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTurmericGingerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTurmericGingerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}