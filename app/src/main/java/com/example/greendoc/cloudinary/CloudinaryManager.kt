package com.example.greendoc.cloudinary

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

object CloudinaryManager {
    fun uploadImage(context: Context, imageUri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val requestId = MediaManager.get().upload(imageUri)
            .unsigned("LeafLog") // Use unsigned preset
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("Cloudinary", "Upload started")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes.toDouble() / totalBytes) * 100
                    Log.d("Cloudinary", "Upload progress: $progress%")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"].toString()
                    Log.d("Cloudinary", "Upload successful: $url")
                    onSuccess(url)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("Cloudinary", "Upload failed: ${error.description}")
                    onError(error.description)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.e("Cloudinary", "Upload rescheduled: ${error.description}")
                }
            })
            .dispatch(context)
    }
}
