package com.example.greendoc.cloudinary

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "drrnzfcir",
            "api_key" to "925759671754125",
            "api_secret" to "PQrVOfqI9J7tw4YYhYidbrSQbl8"
        )

        MediaManager.init(this, config)
    }
}
