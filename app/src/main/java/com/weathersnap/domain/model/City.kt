package com.weathersnap.domain.model

data class City(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val admin1: String? = null
) {
    val displayName: String
        get() = if (admin1 != null) "$name, $admin1, $country" else "$name, $country"
}
