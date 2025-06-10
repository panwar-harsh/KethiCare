package com.example.greendoc



import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.greendoc.databinding.ActivityLightMeterBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LightMeterActivity : ComponentActivity(), SensorEventListener {
    private lateinit var binding: ActivityLightMeterBinding
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Request camera permission and start camera
        requestCameraPermission()

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnClose.setOnClickListener { finish() }

    }
    private fun requestCameraPermission() {
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required!", Toast.LENGTH_SHORT).show()
            }
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
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to open camera!", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.let { lux ->
            binding.tvLuxValue.text = "$lux LUX"
            binding.tvFCValue.text = "${(lux / 10.764).toInt()} FC"

            if (lux < 430) {
                binding.cardLightCondition.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red))
                binding.circleIndicator.setBackgroundResource(R.drawable.red_circle)
                binding.tvLightCondition.text = "Insufficient light conditions for plant growth."
            } else {
                binding.cardLightCondition.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
                binding.circleIndicator.setBackgroundResource(R.drawable.green_circle)
                binding.tvLightCondition.text = "Excellent light conditions for healthy plant growth."
                binding.imgStatusIcon.setBackgroundResource(R.drawable.ic_excellent)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
