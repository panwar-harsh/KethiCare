package com.example.greendoc.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greendoc.R
import com.example.greendoc.Plant
import com.example.greendoc.databinding.ItemPlant2Binding
import java.text.SimpleDateFormat
import java.util.*

class PlantHistoryAdapter(
    private var plants: List<Plant>,
    private val onItemClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantHistoryAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(val binding: ItemPlant2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.apply {
                // Determine display name
                val displayName = when {
                    plant.name != "Detected Plant" && plant.name != "Unknown Plant" -> plant.name
                    plant.disease != null -> plant.disease!!.replace("Healthy", "").trim()
                    else -> "Identified Plant"
                }
                plantName.text = displayName

                // Load image with proper URI handling
                val imageUri = when {
                    plant.imageUrl.startsWith("content://") -> {
                        // Handle content URI - you may need to implement a content resolver
                        plant.imageUrl
                    }
                    plant.imageUrl.isNotEmpty() -> plant.imageUrl
                    else -> null
                }

                Glide.with(root.context)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(plantImage)

                // Format date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dateText.text = dateFormat.format(Date(plant.timestamp))

                // Set status indicators
                when (plant.type) {
                    "identified" -> {
                        statusIcon.setImageResource(R.drawable.main_cardview_img1)
                        statusText.text = "Identified"
                        statusText.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                    }
                    "diagnosed" -> {
                        statusIcon.setImageResource(R.drawable.main_cardview_img3)
                        statusText.text = plant.healthStatus?.capitalize()
                        val colorRes = if (plant.healthStatus == "healthy") R.color.green else R.color.red
                        statusText.setTextColor(ContextCompat.getColor(root.context, colorRes))
                    }
                }

                root.setOnClickListener { onItemClick(plant) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlant2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(plants[position])
    }

    override fun getItemCount() = plants.size

    fun updatePlants(newPlants: List<Plant>) {
        plants = newPlants
        notifyDataSetChanged()
    }
}