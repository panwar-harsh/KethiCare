package com.example.greendoc.Ecommerce

import Tool
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.greendoc.R

class ToolDetailActivity : AppCompatActivity() {

    private lateinit var toolContact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_detail)

        // Find views
        val toolImageView: ImageView = findViewById(R.id.toolImageView)
        val toolNameTextView: TextView = findViewById(R.id.toolNameTextView)
        val toolPriceTextView: TextView = findViewById(R.id.toolPriceTextView)
        val toolCategoryTextView: TextView = findViewById(R.id.toolCategoryTextView)
        val toolNegotiableTextView: TextView = findViewById(R.id.toolNegotiableTextView)
        val toolContactTextView: TextView = findViewById(R.id.toolContactTextView)
        val callSellerButton: Button = findViewById(R.id.callSellerButton)

        val tool: Tool? = intent.getParcelableExtra("tool")
        Log.d("ToolDetailActivity", "Received Tool: $tool")

        tool?.let {
            // Set all the tool details
            toolNameTextView.text = it.name
            toolPriceTextView.text = "Price: ${it.price}"
            toolCategoryTextView.text = "Category: ${it.category}"
            toolNegotiableTextView.text = if (it.negotiable) "Negotiable" else "Fixed Price"

            // Handle contact information
            toolContact = it.contact ?: ""
            if (toolContact.isNotEmpty()) {
                toolContactTextView.text = "Seller Contact: $toolContact"
                callSellerButton.isEnabled = true
            } else {
                toolContactTextView.text = "Seller Contact: Not provided"
                callSellerButton.isEnabled = false
            }

            // Load image
            Glide.with(this)
                .load(it.imageUrl.ifEmpty { R.drawable.image_placeholder_bg })
                .placeholder(R.drawable.image_placeholder_bg)
                .into(toolImageView)

            // Set click listener for call button
            callSellerButton.setOnClickListener {
                if (toolContact.isNotEmpty()) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Permission granted, make the call directly
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:$toolContact")
                        startActivity(callIntent)
                    } else {
                        // Request permission
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CALL_PHONE),
                            CALL_PERMISSION_REQUEST
                        )
                    }
                } else {
                    Toast.makeText(this, "No contact number provided", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e("ToolDetailActivity", "Tool object is null")
            toolNameTextView.text = "Tool not found"
            callSellerButton.isEnabled = false
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$toolContact")
            startActivity(callIntent)
        } else {
            Toast.makeText(this, "Permission denied. Cannot make a call", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CALL_PERMISSION_REQUEST = 101
    }
}
