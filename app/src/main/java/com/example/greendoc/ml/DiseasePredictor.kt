package com.example.greendoc.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DiseasePredictor(private val context: Context) {

    private val MODEL_FILE = "crop_disease_model.tflite"  // TFLite model in assets
    private val LABELS_FILE = "disease_labels.txt"  // Label file in assets
    private val IMAGE_SIZE = 224  // Expected input size for the model
    private lateinit var interpreter: Interpreter
    private val labels = mutableListOf<String>()
    private val EXPECTED_LABEL_COUNT = 38  // Ensure label count matches model output

    init {
        try {
            loadModel()
            loadLabels()
        } catch (e: Exception) {
            Log.e("DiseasePredictor", "Initialization failed: ${e.message}", e)
        }
    }

    // Load TensorFlow Lite model
    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile()
            val options = Interpreter.Options()
            interpreter = Interpreter(modelBuffer, options)
            Log.d("DiseasePredictor", "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e("DiseasePredictor", "Error loading TFLite model: ${e.message}", e)
        }
    }

    // Load the model file from assets
    private fun loadModelFile(): MappedByteBuffer {
        context.assets.openFd(MODEL_FILE).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel: FileChannel = inputStream.channel
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
            }
        }
    }

    // Load class labels from assets
    private fun loadLabels() {
        try {
            context.assets.open(LABELS_FILE).bufferedReader().useLines { lines ->
                labels.clear()
                labels.addAll(lines.map { it.trim() }.filter { it.isNotEmpty() })
            }

            Log.d("DiseasePredictor", "Labels loaded: ${labels.size} labels found")

            // Ensure the correct number of labels
            if (labels.size != EXPECTED_LABEL_COUNT) {
                Log.e("DiseasePredictor", "Warning: Expected $EXPECTED_LABEL_COUNT labels, but found ${labels.size}")
                if (labels.size > EXPECTED_LABEL_COUNT) {
                    labels.subList(EXPECTED_LABEL_COUNT, labels.size).clear() // Trim extra labels
                    Log.w("DiseasePredictor", "Extra labels trimmed to match model output size")
                }
            }
        } catch (e: Exception) {
            Log.e("DiseasePredictor", "Error loading labels: ${e.message}", e)
        }
    }

    fun predictDisease(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // Ensure output size matches model output [1, 38]
        val outputArray = Array(1) { FloatArray(EXPECTED_LABEL_COUNT) }

        Log.d("DiseasePredictor", "🟢 Running model inference...")

        return try {
            interpreter.run(inputBuffer, outputArray)

            // Find the highest probability index
            val maxIndex = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1
            val confidence = if (maxIndex != -1) outputArray[0][maxIndex] else 0f

            // Log prediction details
            Log.d("DiseasePredictor", "All predictions:")
            outputArray[0].forEachIndexed { index, confidence ->
                Log.d("DiseasePredictor", "Label: ${labels.getOrNull(index) ?: "Unknown"}, Confidence: $confidence")
                Log.d("DiseasePredictor", "${labels.getOrNull(index)}: $confidence")
            }

            if (maxIndex in labels.indices && confidence > 0.5f) {
                Log.d("DiseasePredictor", "Predicted: ${labels[maxIndex]} with confidence: $confidence")
                labels[maxIndex]
            } else {
                "Unknown Disease"
            }
        } catch (e: Exception) {
            Log.e("DiseasePredictor", "Model inference error: ${e.message}", e)
            "Prediction Failed"
        }
    }

    fun predictPlant(bitmap: Bitmap): String {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
        val outputArray = Array(1) { FloatArray(EXPECTED_LABEL_COUNT) }

        try {
            interpreter.run(inputBuffer, outputArray)

            // Enhanced debugging:
            Log.d("PredictPlant", "Raw output: ${outputArray[0].contentToString()}")

            val maxIndex = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1
            val confidence = outputArray[0][maxIndex]

            Log.d("PredictPlant", "Max index: $maxIndex, Confidence: $confidence")
            Log.d("PredictPlant", "Possible label: ${labels.getOrNull(maxIndex)}")

            return if (maxIndex in labels.indices && confidence > 0.5f) {
                labels[maxIndex]
            } else {
                "Unknown Plant (low confidence: $confidence)"
            }
        } catch (e: Exception) {
            Log.e("PredictPlant", "Error during prediction", e)
            return "Prediction Error"
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        val preparedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // Ensure correct format
        preparedBitmap.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            buffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            buffer.putFloat((pixel and 0xFF) / 255.0f)
        }
        Log.d("DiseasePredictor", "Image converted to ByteBuffer")
        return buffer
    }
}
