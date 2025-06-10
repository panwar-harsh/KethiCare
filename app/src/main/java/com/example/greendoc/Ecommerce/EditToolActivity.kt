package com.example.greendoc.Ecommerce

import Tool
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.greendoc.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.*
import java.io.InputStream
import java.util.concurrent.Executors

class EditToolActivity : AppCompatActivity() {
    private lateinit var toolImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var negotiableCheckBox: CheckBox
    private lateinit var contactEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var submitButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var database: DatabaseReference

    private var toolId: String? = null
    private var imageUrl: String? = null
    private var imageUri: Uri? = null

    private val cloudinary = Cloudinary("cloudinary://API_KEY:API_SECRET@CLOUD_NAME")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tool)

        database = FirebaseDatabase.getInstance().reference.child("tools")

        // Initialize views
        toolImageView = findViewById(R.id.toolImageView)
        nameEditText = findViewById(R.id.toolNameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        negotiableCheckBox = findViewById(R.id.negotiableCheckBox)
        contactEditText = findViewById(R.id.contactEditText)
        uploadButton = findViewById(R.id.uploadButton)
        submitButton = findViewById(R.id.submitButton)
        progressBar = findViewById(R.id.progressBar)

        toolId = intent.getStringExtra("toolId")

        toolId?.let { fetchToolDetails(it) }

        toolImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(1080, 1080)
                .start()
        }

        uploadButton.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToCloudinary(uri)
            } ?: Toast.makeText(this, "Please select an image first!", Toast.LENGTH_SHORT).show()
        }

        submitButton.setOnClickListener {
            updateToolDetails()
        }
    }

    private fun fetchToolDetails(toolId: String) {
        database.child(toolId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tool = snapshot.getValue(Tool::class.java)
                    tool?.let {
                        it.id = snapshot.key ?: ""
                        nameEditText.setText(it.name)
                        priceEditText.setText(it.price)
                        contactEditText.setText(it.contact)
                        negotiableCheckBox.isChecked = it.negotiable
                        imageUrl = it.imageUrl

                        val categories = resources.getStringArray(R.array.tool_categories)
                        categorySpinner.setSelection(categories.indexOf(it.category).coerceAtLeast(0))

                        Glide.with(this@EditToolActivity).load(it.imageUrl).into(toolImageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditToolActivity, "Failed to load tool details", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        progressBar.visibility = ProgressBar.VISIBLE

        Executors.newSingleThreadExecutor().execute {
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                imageUrl = uploadResult["secure_url"].toString()

                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this, "Image Uploaded!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this, "Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateToolDetails() {
        val name = nameEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val negotiable = negotiableCheckBox.isChecked
        val contact = contactEditText.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTool = mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "negotiable" to negotiable,
            "contact" to contact,
            "imageUrl" to imageUrl
        )

        toolId?.let { id ->
            database.child(id)
                .updateChildren(updatedTool)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tool Updated Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update tool", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            toolImageView.setImageURI(imageUri)
        }
    }
}
