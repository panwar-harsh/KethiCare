package com.example.greendoc.Ecommerce

import Tool
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.greendoc.R

class ToolAdapter(
    private var tools: List<Tool>,
    private val onItemClick: (Tool) -> Unit
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    inner class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolImage: ImageView = itemView.findViewById(R.id.toolImageView)
        val toolName: TextView = itemView.findViewById(R.id.toolNameTextView)
        val toolPrice: TextView = itemView.findViewById(R.id.toolPriceTextView)
        val toolNegotiable: TextView = itemView.findViewById(R.id.toolNegotiableTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tool, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = tools[position]

        holder.toolName.text = tool.name
        holder.toolPrice.text = "Price: ${tool.price}"
        holder.toolNegotiable.text = if (tool.negotiable) "Negotiable" else "Fixed Price"

        // Load tool image safely
        Glide.with(holder.toolImage)
            .load(tool.imageUrl ?: R.drawable.image_placeholder_bg) // Handle null URLs
            .placeholder(R.drawable.image_placeholder_bg) // Show placeholder while loading
            .error(R.drawable.image_placeholder_bg) // Show placeholder if loading fails
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache images for better performance
            .into(holder.toolImage)

        // Click listener for opening details
        holder.itemView.setOnClickListener { onItemClick(tool) }
    }

    override fun getItemCount() = tools.size

    fun updateList(newList: List<Tool>) {
        tools = newList
        notifyItemRangeChanged(0, tools.size) // More efficient than notifyDataSetChanged()
    }
}
