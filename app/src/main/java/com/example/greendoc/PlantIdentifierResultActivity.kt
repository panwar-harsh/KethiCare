package com.example.greendoc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.greendoc.databinding.ActivityPlantIdentifierResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class PlantIdentifierResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantIdentifierResultBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantIdentifierResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plantName = intent.getStringExtra("plant_name") ?: "Unknown Plant"
        val imageUrl = intent.getStringExtra("image_uri")

        // Load image
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.resultImageView)
        }

        binding.plantNameTextView.text = plantName

        binding.diagnoseCardview.setOnClickListener {
            startActivity(Intent(this, CameraActivityDiagnose::class.java))
        }

        binding.AddToMyPlantButton.setOnClickListener {
            if (plantName != null) {
                savePlantToGarden(plantName, imageUrl ?: "")
            } else {
                Toast.makeText(this, "Plant name not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Load plant details from JSON
        val plantInfo = plantName?.let { loadPlantInfo(it) }

        plantInfo?.let { info ->
            updateUIWithPlantInfo(info)
        } ?: showUnknownPlant()
    }

    private fun updateUIWithPlantInfo(info: PlantInfo) {
        binding.apply {
            plantNameTextView.text = info.plant
            valueTemperature.text = "Ideal Temperature: ${info.temperature}"
            valueSunlight.text = "Sunlight Requirements: ${info.sunlight}"
            valueWater.text = "Watering Needs: ${info.watering}"
            valuePotting.text = "Repotting: ${info.repotting}"
            valueFertilizer.text = "Fertilizing: ${info.fertilizing}"
            valuePests.text = info.pests?.joinToString(", ")?.let {
                "Common Pests: $it"
            } ?: "No pests information"
        }
    }

    fun savePlantToGarden(plantName: String, plantImageUrl: String) {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Please login to save plants", Toast.LENGTH_SHORT).show()
            return
        }

        val plant = Plant(
            name = plantName,
            imageUrl = plantImageUrl,
            type = "identified",
            timestamp = System.currentTimeMillis()
        )
        // Generate a new unique key that will be used for both garden and history
        val newPlantKey = database.child("user_plants").child(userId).child("garden").push().key
            ?: return // Return if key generation fails

        // Save to garden node
        database.child("user_plants")
            .child(userId)
            .child("garden")
            .child(newPlantKey)
            .setValue(plant.copy(id = newPlantKey))
            .addOnSuccessListener {
                // After successful save to garden, also save to history
                database.child("user_plants")
                    .child(userId)
                    .child("history")
                    .child(newPlantKey)
                    .setValue(plant.copy(id = newPlantKey))
                    .addOnCompleteListener {
                        Toast.makeText(this, "Plant saved successfully", Toast.LENGTH_SHORT).show()
                        navigateToMainActivity()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save plant", Toast.LENGTH_SHORT).show()
                Log.e("SavePlant", "Error saving plant", e)
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("UPDATE_GARDEN_FRAGMENT", true)
        }
        startActivity(intent)
        finish()
    }

    private fun loadPlantInfo(plantName: String): PlantInfo? {
        return try {
            assets.open("plant_diseases.json").bufferedReader().use { reader ->
                val type = object : TypeToken<Map<String, PlantInfo>>() {}.type
                val plantMap: Map<String, PlantInfo> = Gson().fromJson(reader, type)
                plantMap[plantName]
            }
        } catch (e: IOException) {
            Log.e("PlantIdentifier", "Error loading plant info", e)
            null
        }
    }

    private fun showUnknownPlant() {
        binding.apply {
            plantNameTextView.text = "Unknown Plant"
            valueTemperature.text = "Information not available"
            valueSunlight.text = "Information not available"
            valueWater.text = "Information not available"
            valuePotting.text = "Information not available"
            valueFertilizer.text = "Information not available"
            valuePests.text = "Could not identify plant species"
        }
    }
}

data class PlantInfo(
    val plant: String,
    val temperature: String,
    val sunlight: String,
    val watering: String,
    val repotting: String,
    val fertilizing: String,
    val pests: List<String>? = null
)