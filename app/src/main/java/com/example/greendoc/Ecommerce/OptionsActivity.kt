package com.example.greendoc.Ecommerce

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners for each CardView
        binding.homeScreen.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }

        binding.uploadToolButton.setOnClickListener {
            startActivity(Intent(this, UploadToolActivity::class.java))
        }

        binding.editToolButton.setOnClickListener {
            startActivity(Intent(this, EditToolActivity::class.java))
        }

        binding.listToolButton.setOnClickListener {
            startActivity(Intent(this, ToolListActivity::class.java))
        }

//        binding.deleteToolButton.setOnClickListener {
//            startActivity(Intent(this, DeleteToolActivity::class.java))
//        }

//        binding.historyToolButton.setOnClickListener {
//            startActivity(Intent(this, HistoryActivity::class.java))
//        }
    }
}