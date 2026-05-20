package com.weathersnap.domain.model

data class Report(
    val id: Long = 0,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val imagePath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val notes: String,
    val timestamp: Long,
    val isDraft: Boolean = false
)
