package com.example.greendoc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityWaterCalculatorBinding

class WaterCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.selectPlantButton.setOnClickListener {
            val bottomSheet = SelectPlantBottomSheet.newInstance()
            bottomSheet.setOnPlantSelectedListener { selectedPlant ->
                val intent = Intent(this, WaterStepsActivity::class.java).apply {
                    putExtra("plantId", selectedPlant.id)
                    putExtra("plantName", selectedPlant.name)
                    putExtra("plantImageUrl", selectedPlant.imageUrl)
                }
                startActivity(intent)
            }
            bottomSheet.show(supportFragmentManager, "SelectPlantBottomSheet")
        }
    }
}
