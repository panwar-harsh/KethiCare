package com.example.greendoc.Ecommerce

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.greendoc.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UploadToolActivity : AppCompatActivity() {
    private lateinit var toolNameEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categorySpinner: AppCompatSpinner
    private lateinit var negotiableCheckBox: CheckBox
    private lateinit var submitButton: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private lateinit var database: FirebaseDatabase
    private val auth = FirebaseAuth.getInstance()
    private val categories = listOf("Tractors", "Plows", "Irrigation", "Other")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_tool)

        database = FirebaseDatabase.getInstance()

        toolNameEditText = findViewById(R.id.toolNameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        contactEditText = findViewById(R.id.contactEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        negotiableCheckBox = findViewById(R.id.negotiableCheckBox)
        submitButton = findViewById(R.id.submitButton)
        imageView = findViewById(R.id.toolImageView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = adapter

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        submitButton.setOnClickListener {
            uploadTool()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadTool() {
        val name = toolNameEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val negotiable = negotiableCheckBox.isChecked
        val sellerId = auth.currentUser?.uid ?: ""
        val userPhoneNumber = contactEditText.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || imageUri == null || userPhoneNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

        MediaManager.get().upload(imageUri).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                val imageUrl = resultData["secure_url"] as String
                Log.d("Cloudinary", "Image uploaded successfully: $imageUrl")
                saveToolToDatabase(name, price, category, negotiable, imageUrl, sellerId, userPhoneNumber)
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.e("Cloudinary", "Image upload failed: ${error?.description}")
                Toast.makeText(
                    this@UploadToolActivity,
                    "Image upload failed: ${error?.description}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onStart(requestId: String?) {}
            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
            override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
        }).dispatch()
    }

    private fun saveToolToDatabase(
        name: String,
        price: String,
        category: String,
        negotiable: Boolean,
        imageUrl: String,
        sellerId: String,
        contact: String
    ) {
        val tool = hashMapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "negotiable" to negotiable,
            "imageUrl" to imageUrl,
            "sellerId" to sellerId,
            "contact" to contact
        )

        val newToolRef = database.reference.child("tools").push()
        newToolRef.setValue(tool)
            .addOnSuccessListener {
                Toast.makeText(this, "Tool uploaded successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload tool", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }
}
