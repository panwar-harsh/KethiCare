package com.example.greendoc

import androidx.annotation.Keep

@Keep
data class Plant(
    var id: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var type: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var healthStatus: String? = null,
    var disease: String? = null,
    val deleted: Boolean = false
)