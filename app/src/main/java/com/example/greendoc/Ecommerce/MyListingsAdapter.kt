package com.example.greendoc.Ecommerce

import Tool
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greendoc.R
import com.google.firebase.database.FirebaseDatabase

class MyListingsAdapter(
    private val context: Context,
    private val toolList: MutableList<Tool>
) : RecyclerView.Adapter<MyListingsAdapter.ViewHolder>() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_my_listing, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tool = toolList[position]

        Glide.with(context).load(tool.imageUrl).into(holder.toolImageView)
        holder.toolNameTextView.text = tool.name
        holder.toolPriceTextView.text = "₹${tool.price}"
        holder.toolCategoryTextView.text = tool.category

        holder.editButton.setOnClickListener {
            val intent = Intent(context, EditToolActivity::class.java)
            intent.putExtra("toolId", tool.id)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            database.reference.child("tools").child(tool.id)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    toolList.removeAt(position)
                    notifyItemRemoved(position)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to Delete!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = toolList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val toolImageView: ImageView = itemView.findViewById(R.id.toolImageView)
        val toolNameTextView: TextView = itemView.findViewById(R.id.toolNameTextView)
        val toolPriceTextView: TextView = itemView.findViewById(R.id.toolPriceTextView)
        val toolCategoryTextView: TextView = itemView.findViewById(R.id.toolCategoryTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}
