package com.weathersnap.data.repository

import com.google.gson.Gson
import com.weathersnap.data.local.CachedCityDao
import com.weathersnap.data.local.CachedCityEntity
import com.weathersnap.data.remote.OpenMeteoApi
import com.weathersnap.data.remote.dto.GeocodingResponse
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import com.weathersnap.util.WeatherCodeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: OpenMeteoApi,
    private val cachedCityDao: CachedCityDao,
    private val gson: Gson
) {
    companion object {
        private const val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    }

    suspend fun searchCities(query: String): Result<List<City>> = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            val minTimestamp = System.currentTimeMillis() - CACHE_DURATION_MS
            val cached = cachedCityDao.getCachedResult(query.lowercase(), minTimestamp)
            if (cached != null) {
                val response = gson.fromJson(cached.responseJson, GeocodingResponse::class.java)
                return@withContext Result.success(mapCities(response))
            }

            // Fetch from API
            val response = api.searchCities(query)

            // Cache the response
            cachedCityDao.insertCache(
                CachedCityEntity(
                    query = query.lowercase(),
                    responseJson = gson.toJson(response),
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(mapCities(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeather(city: City): Result<Weather> = withContext(Dispatchers.IO) {
        try {
            val response = api.getWeather(city.latitude, city.longitude)
            val current = response.current
                ?: return@withContext Result.failure(Exception("No weather data available"))

            val weatherCode = current.weatherCode ?: 0
            val weather = Weather(
                cityName = city.name,
                country = city.country,
                temperature = current.temperature ?: 0.0,
                conditionCode = weatherCode,
                condition = WeatherCodeMapper.getCondition(weatherCode),
                humidity = current.humidity ?: 0,
                windSpeed = current.windSpeed ?: 0.0,
                pressure = current.pressure ?: 0.0,
                latitude = city.latitude,
                longitude = city.longitude
            )
            Result.success(weather)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapCities(response: GeocodingResponse): List<City> {
        return response.results?.map { result ->
            City(
                name = result.name,
                country = result.country ?: "",
                latitude = result.latitude,
                longitude = result.longitude,
                admin1 = result.admin1
            )
        } ?: emptyList()
    }
}
