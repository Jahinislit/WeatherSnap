package com.weathersnap.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather? = null
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double? = null,
    @SerializedName("relative_humidity_2m") val humidity: Int? = null,
    @SerializedName("weather_code") val weatherCode: Int? = null,
    @SerializedName("wind_speed_10m") val windSpeed: Double? = null,
    @SerializedName("surface_pressure") val pressure: Double? = null
)
