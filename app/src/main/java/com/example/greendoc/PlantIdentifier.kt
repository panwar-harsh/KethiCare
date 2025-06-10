package com.example.greendoc

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.greendoc.cloudinary.CloudinaryManager
import com.example.greendoc.databinding.ActivityPlantIdentifierBinding
import com.example.greendoc.ml.DiseasePredictor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlantIdentifier : AppCompatActivity() {
    private lateinit var binding: ActivityPlantIdentifierBinding
    private lateinit var diseasePredictor: DiseasePredictor
    private var cameraExecutor: ExecutorService? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { processImageFromUri(it) } ?: showToast("No image selected")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantIdentifierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diseasePredictor = DiseasePredictor(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        requestCameraPermission()
        startGreenLineAnimation()

        binding.apply {
            btnClose.setOnClickListener { finish() }
            btnInfo.setOnClickListener { showInfoBottomSheet() }
            btnGallery.setOnClickListener { openGallery() }
            btnFlash.setOnClickListener { toggleFlash() }
            btnCapture.setOnClickListener { captureImage() }
            btnDiagnose.setOnClickListener {
                val intent = Intent(this@PlantIdentifier, CameraActivityDiagnose::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun requestCameraPermission() {
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) startCamera() else showToast("Camera permission is required!")
            }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = convertImageProxyToBitmap(image)
                    bitmap?.let { processCapturedImage(it) } ?: showToast("Failed to process image")
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Capture failed")
                    Log.e("PlantIdentifier", "Image capture failed", exception)
                }
            }) ?: showToast("Camera not initialized")
    }

    private fun processImageFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap?.let { processCapturedImage(it) }
        } catch (e: IOException) {
            Log.e("PlantIdentifier", "Error loading image", e)
            showToast("Failed to load image")
        }
    }

    private fun processCapturedImage(imageBitmap: Bitmap) {
        try {
            // Step 1: Run prediction
            val plantName = diseasePredictor.predictPlant(imageBitmap)

            // Step 2: Save bitmap to a temporary file
            val file = File(cacheDir, "captured_plant.jpg")
            val outputStream = FileOutputStream(file)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            // Step 3: Get URI for the saved file
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

            // Step 4: Upload to Cloudinary
            CloudinaryManager.uploadImage(
                context = this,
                imageUri = uri,
                onSuccess = { imageUrl ->
                    // Step 5: Navigate to result screen with Cloudinary URL and plant name
                    val intent = Intent(this, PlantIdentifierResultActivity::class.java).apply {
                        putExtra("image_uri", imageUrl) // Cloudinary image URL
                        putExtra("plant_name", plantName)
                    }
                    startActivity(intent)
                },
                onError = { error ->
                    // Handle upload failure
                    showToast("Image upload failed: $error")
                    Log.e("Cloudinary", "Upload failed: $error")
                }
            )
        } catch (e: Exception) {
            showToast("Something went wrong while processing the image")
            Log.e("PlantIdentifier", "Error processing captured image", e)
        }
    }


    private fun convertImageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e("PlantIdentifier", "Error converting image", e)
            null
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun toggleFlash() {
        camera?.cameraControl?.enableTorch(camera?.cameraInfo?.torchState?.value == TorchState.OFF)
    }

    private fun showInfoBottomSheet() {
        showToast("Info feature not yet implemented")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun startGreenLineAnimation() {
        val greenLine = binding.greenLine
        binding.cameraPreview.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.cameraPreview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val parentHeight = binding.cameraPreview.height.toFloat()
                val animator = ValueAnimator.ofFloat(0f, parentHeight).apply {
                    duration = 2500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        greenLine.translationY = animation.animatedValue as Float
                    }
                }
                animator.start()
            }
        })
    }
}
