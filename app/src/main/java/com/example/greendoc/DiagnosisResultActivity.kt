package com.example.greendoc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.greendoc.cloudinary.CloudinaryManager
import com.example.greendoc.databinding.ActivityDiagnosisResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class DiagnosisResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnosisResultBinding
    private var currentPlantName: String? = null
    private var currentPlant: Plant? = null // Added to store plant reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosisResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }
        val diseaseLabel = intent.getStringExtra("disease_labels")
        currentPlantName = intent.getStringExtra("plant_name")

        // Set image
        imageUri?.let {
            binding.resultImageView.setImageURI(it)
        }

        // Load disease details from JSON
        val diseaseInfo = loadDiseaseInfo()

        diseaseLabel?.let { label ->
            diseaseInfo[label]?.let { info ->
                // Update UI elements
                if (label.endsWith("Healthy")) {
                    binding.healthStatusTextView.text = "✅ Your plant is healthy"
                    binding.healthStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
                } else {
                    binding.healthStatusTextView.text = "⚠️ Your plant is not healthy"
                    binding.healthStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
                }

                binding.checkRegularlyTextView.text = "Check regularly to see if your plant is improving"
                binding.diseaseInfoTextView.text = "${info.disease}\n\n${info.plant}"
                binding.causesContentTextView.text = info.causes?.let { formatCauses(it) } ?: "No causes information available"
                binding.preventionContentTextView.text = formatPreventionSteps(info.prevention)

                // SAVE THE DIAGNOSED PLANT HERE (after we have all info but before click listeners)
                saveDiagnosedPlant(diseaseLabel, imageUri)

            } ?: showUnknownDisease()
        } ?: showUnknownDisease()

        // Set up click listeners AFTER saving
        binding.retakeButton.setOnClickListener {
            startActivity(Intent(this, CameraActivityDiagnose::class.java))
            finish()
        }

        binding.plantInfoButton.setOnClickListener {
            currentPlant?.let { plant ->
                val intent = Intent(this, PlantIdentifierResultActivity::class.java).apply {
                    putExtra("plant_name", plant.name)
                    putExtra("image_uri", plant.imageUrl)
                    putExtra("disease_info", plant.disease)
                    putExtra("health_status", plant.healthStatus)
                    putExtra("from_diagnosis", true)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Plant information not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatCauses(causes: List<String>): String {
        return causes.joinToString("\n") { "• $it" }
    }

    private fun loadDiseaseInfo(): Map<String, DiseaseInfo> {
        return try {
            assets.open("plant_diseases.json").bufferedReader().use {
                val type = object : TypeToken<Map<String, DiseaseInfo>>() {}.type
                Gson().fromJson(it.readText(), type)
            }
        } catch (e: IOException) {
            Log.e("DiagnosisResult", "Error loading disease info", e)
            emptyMap()
        }
    }

    private fun formatPreventionSteps(steps: List<String>): String {
        return steps.joinToString("\n") { "• $it" }
    }

    private fun saveDiagnosedPlant(diseaseLabel: String?, imageUri: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val plantName = currentPlantName ?: "Unknown Plant"

        if (imageUri?.scheme == "content") {
            // Handle content URI by uploading to Cloudinary first
            CloudinaryManager.uploadImage(
                context = this,
                imageUri = imageUri,
                onSuccess = { imageUrl ->
                    // After successful upload, save plant with Cloudinary URL
                    savePlantToDatabase(userId, plantName, diseaseLabel, imageUrl)
                },
                onError = { error ->
                    Log.e("DiagnosisResult", "Cloudinary upload failed: $error")
                    // Fallback to saving with content URI if upload fails
                    savePlantToDatabase(userId, plantName, diseaseLabel, imageUri.toString())
                }
            )
        } else {
            // Directly save if not a content URI
            savePlantToDatabase(userId, plantName, diseaseLabel, imageUri?.toString() ?: "")
        }
    }

    private fun savePlantToDatabase(userId: String, plantName: String, diseaseLabel: String?, imageUrl: String) {
        val plant = Plant(
            name = plantName,
            imageUrl = imageUrl,
            type = "diagnosed",
            healthStatus = if (diseaseLabel?.endsWith("Healthy") == true) "healthy" else "unhealthy",
            disease = diseaseLabel,
            timestamp = System.currentTimeMillis(),
            deleted = false
        )

        val newPlantKey = FirebaseDatabase.getInstance().reference
            .child("user_plants")
            .child(userId)
            .child("garden")
            .push()
            .key ?: return

        plant.id = newPlantKey
        this.currentPlant = plant // Store the plant reference for later use

        // Save to garden node
        FirebaseDatabase.getInstance().reference
            .child("user_plants")
            .child(userId)
            .child("garden")
            .child(newPlantKey)
            .setValue(plant)
            .addOnSuccessListener {
                // After successful save to garden, save to history
                FirebaseDatabase.getInstance().reference
                    .child("user_plants")
                    .child(userId)
                    .child("history")
                    .child(newPlantKey)
                    .setValue(plant)
                    .addOnSuccessListener {
                        Log.d("DiagnosisResult", "Diagnosis saved to both garden and history")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DiagnosisResult", "Failed to save to history", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("DiagnosisResult", "Failed to save diagnosis to garden", e)
            }
    }

    private fun showUnknownDisease() {
        binding.healthStatusTextView.text = "⚠️ Unknown Plant Status"
        binding.checkRegularlyTextView.text = "Please try with a clearer image"
        binding.diseaseInfoTextView.text = "No specific disease identified"
        binding.causesContentTextView.text = "Could not determine plant health status"
        binding.preventionContentTextView.text = "• Try capturing the image again\n• Ensure good lighting\n• Focus on affected leaves"
    }
}

data class DiseaseInfo(
    val plant: String,
    val disease: String,
    val causes: List<String>,
    val prevention: List<String>
)