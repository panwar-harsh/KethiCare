package com.example.greendoc.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greendoc.Plant
import com.example.greendoc.R

class PlantAdapter(
    private var plants: List<Plant>,
    private val onItemClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val plantName: TextView = itemView.findViewById(R.id.tvPlantName)
        private val plantImage: ImageView = itemView.findViewById(R.id.ivPlantImage)


        fun bind(plant: Plant) {
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

            Glide.with(itemView.context)
                .load(imageUri)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(plantImage)
            itemView.setOnClickListener { onItemClick(plant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
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