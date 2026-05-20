package com.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_cities")
data class CachedCityEntity(
    @PrimaryKey val query: String,
    val responseJson: String,
    val timestamp: Long
)
