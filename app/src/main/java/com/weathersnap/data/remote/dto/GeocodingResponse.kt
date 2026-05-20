package com.weathersnap.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("results") val results: List<GeocodingResult>? = null
)

data class GeocodingResult(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("country") val country: String? = null,
    @SerializedName("admin1") val admin1: String? = null,
    @SerializedName("country_code") val countryCode: String? = null
)
