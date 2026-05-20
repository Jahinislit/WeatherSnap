package com.weathersnap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReportEntity::class, CachedCityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherSnapDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
    abstract fun cachedCityDao(): CachedCityDao
}
