package com.example.greendoc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greendoc.databinding.ActivityWaterStepsBinding
import com.example.greendoc.watercalculator.StepPotVolumeFragment

class WaterStepsActivity : AppCompatActivity() {

    lateinit var binding: ActivityWaterStepsBinding

    // These store user input across fragments
    var plantLocation: String = "Indoor"
    var humidity: Int = 50
    var temperature: Int = 25
    var potVolume: Double = 1.0  // in liters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterStepsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager with Fragments
        val fragments = listOf(
            StepLocationFragment(),
            StepHumidityFragment(),
            StepTemperatureFragment(),
            StepPotVolumeFragment()
        )

        val adapter = WaterStepPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Disable swipe
    }

    fun setPotVolume(valueInLitres: Double, s: String) {
        potVolume = valueInLitres
    }


    fun goToNextStep() {
        val currentItem = binding.viewPager.currentItem
        if (currentItem < 3) {
            binding.viewPager.currentItem = currentItem + 1
        } else {
            calculateWaterRequirement()
        }
    }

    private fun calculateWaterRequirement() {
        // Sample calculation logic — adjust as needed
        val baseWater = potVolume * 100  // base value in ml
        val humidityFactor = (100 - humidity) / 100.0
        val tempFactor = temperature / 30.0
        val locationFactor = if (plantLocation == "Outdoor") 1.2 else 1.0

        val waterAmount = baseWater * humidityFactor * tempFactor * locationFactor

        // Show result activity
        val intent = WaterResultActivity.newIntent(this, waterAmount)
        startActivity(intent)
        finish()
    }
}
