package com.weathersnap.domain.model

data class Weather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val conditionCode: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val latitude: Double,
    val longitude: Double
)
