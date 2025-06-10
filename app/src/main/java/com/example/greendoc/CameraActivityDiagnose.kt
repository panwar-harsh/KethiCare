package com.example.greendoc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.greendoc.databinding.ActivityCameraDiagnoseBinding
import com.example.greendoc.ml.DiseasePredictor  // Import DiseasePredictor
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Size
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.abs

class CameraActivityDiagnose : AppCompatActivity() {
    private lateinit var binding: ActivityCameraDiagnoseBinding
    private var cameraExecutor: ExecutorService? = null
    private var imageCapture: ImageCapture? = null
    private var isFlashOn = false
    private var camera: Camera? = null  // Store camera instance
    private lateinit var diseasePredictor: DiseasePredictor  // Initialize TensorFlow Lite model



    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)  // Open input stream
                    val bitmap = BitmapFactory.decodeStream(inputStream)  // Convert to Bitmap
                    inputStream?.close()

                    if (bitmap != null && isImageIdentifiable(bitmap)) {
                        analyzeImage(bitmap)
                    } else {
                        showToast("Selected image is not clear!")
                    }
                } catch (e: IOException) {
                    Log.e("CameraActivity", "Error loading image from gallery", e)
                    showToast("Failed to load image")
                }
            } else {
                showToast("No image selected")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        diseasePredictor = DiseasePredictor(this)  // Initialize ML model

        // Request camera permission and start camera
        requestCameraPermission()
        // Ensure the red line animation starts after layout is measured
        binding.cameraPreview.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.cameraPreview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startRedLineAnimation()  // Start animation only when layout is ready
            }
        })

        binding.apply {
            btnClose.setOnClickListener { finish() }
            btnInfo.setOnClickListener { showInfoBottomSheet() }
            btnGallery.setOnClickListener { openGallery() }
            btnFlash.setOnClickListener { toggleFlash() }
            btnCapture.setOnClickListener { captureImage() }
            btnIdentier.setOnClickListener {
                val intent = Intent(this@CameraActivityDiagnose, PlantIdentifier::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun startRedLineAnimation() {
        val redLine = binding.redLine

        // Ensure the animation runs only after layout is measured
        binding.cameraPreview.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.cameraPreview.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val parentHeight = binding.cameraPreview.height.toFloat()

                val animator = ValueAnimator.ofFloat(0f, parentHeight).apply {
                    duration = 2500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    interpolator = LinearInterpolator()
                    addUpdateListener { animation ->
                        redLine.translationY = animation.animatedValue as Float
                    }
                }
                animator.start()
            }
        })
    }

    private fun requestCameraPermission() {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera() else showToast("Camera permission is required!")
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Bind everything to lifecycle
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }


    private fun showInfoBottomSheet() {
        InfoBottomSheetFragment().show(supportFragmentManager, "InfoBottomSheet")
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun toggleFlash() {
        camera?.let {
            isFlashOn = !isFlashOn
            it.cameraControl.enableTorch(isFlashOn)  // Enable or disable torch
            showToast(if (isFlashOn) "Flash On" else "Flash Off")
        } ?: showToast("Flash not available")
    }

    private fun captureImage() {
        imageCapture?.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = convertImageProxyToBitmap(image)
                    if (bitmap != null && isImageIdentifiable(bitmap)) {
                        val detectedDisease = predictDisease(bitmap)
                        openDiagnosisResult(bitmap, detectedDisease)
                    } else {
                        showToast("Unclear image, please try again!")
                    }
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Capture failed")
                    Log.e("CameraActivity", "Image capture failed", exception)
                }
            }) ?: showToast("Camera not initialized")
    }

    private fun openDiagnosisResult(bitmap: Bitmap, diseaseName: String) {
        try {
            // Save Bitmap to a temporary file
            val file = File(cacheDir, "diagnosed_image.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Convert file to URI
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

            // Pass URI instead of Bitmap
            val intent = Intent(this, DiagnosisResultActivity::class.java).apply {
                putExtra("image_uri", uri.toString())  // Pass the image URI
                putExtra("disease_labels", diseaseName)
                putExtra("plant_name", "Detected Plant")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("CameraActivityDiagnose", "Error opening diagnosis result", e)
        }
    }


    private fun convertImageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e("CameraActivity", "Error converting image", e)
            null
        }
    }

    private fun isImageIdentifiable(bitmap: Bitmap): Boolean {
        // 1. Basic size check
        if (bitmap.width < 224 || bitmap.height < 224) {
            Log.d("ImageValidation", "Failed: Image too small")
            return false
        }

        // 2. Simplified quality checks (remove edge detection temporarily)
        return true // Temporarily accept all images to test

    }

    private fun analyzeImage(bitmap: Bitmap) {
        val predictedDisease = predictDisease(bitmap)
        openDiagnosisResult(bitmap, predictedDisease)
    }

    private fun predictDisease(bitmap: Bitmap): String {
        return try {
            diseasePredictor.predictDisease(bitmap) ?: "Unknown Disease"
        } catch (e: Exception) {
            Log.e("CameraActivityDiagnose", "Model Prediction Error", e)
            "Prediction Failed"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
        private const val MIN_EDGE_THRESHOLD = 0.1f // Adjust this value based on testing
        private const val EDGE_DETECTION_THRESHOLD = 50 // Pixel intensity difference to consider an edge
    }
}

