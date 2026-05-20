package com.weathersnap.util

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherCodeMapperTest {

    @Test
    fun `clear sky returns correct condition`() {
        assertEquals("Clear sky", WeatherCodeMapper.getCondition(0))
    }

    @Test
    fun `mainly clear returns correct condition`() {
        assertEquals("Mainly clear", WeatherCodeMapper.getCondition(1))
    }

    @Test
    fun `partly cloudy returns correct condition`() {
        assertEquals("Partly cloudy", WeatherCodeMapper.getCondition(2))
    }

    @Test
    fun `overcast returns correct condition`() {
        assertEquals("Overcast", WeatherCodeMapper.getCondition(3))
    }

    @Test
    fun `fog returns correct condition`() {
        assertEquals("Foggy", WeatherCodeMapper.getCondition(45))
        assertEquals("Foggy", WeatherCodeMapper.getCondition(48))
    }

    @Test
    fun `rain codes return correct condition`() {
        assertEquals("Rain", WeatherCodeMapper.getCondition(61))
        assertEquals("Rain", WeatherCodeMapper.getCondition(63))
        assertEquals("Rain", WeatherCodeMapper.getCondition(65))
    }

    @Test
    fun `thunderstorm returns correct condition`() {
        assertEquals("Thunderstorm", WeatherCodeMapper.getCondition(95))
        assertEquals("Thunderstorm with hail", WeatherCodeMapper.getCondition(96))
    }

    @Test
    fun `unknown code returns Unknown`() {
        assertEquals("Unknown", WeatherCodeMapper.getCondition(999))
    }

    @Test
    fun `emojis are returned for valid codes`() {
        assertEquals("☀️", WeatherCodeMapper.getEmoji(0))
        assertEquals("⛅", WeatherCodeMapper.getEmoji(2))
        assertEquals("☁️", WeatherCodeMapper.getEmoji(3))
        assertEquals("🌧️", WeatherCodeMapper.getEmoji(61))
        assertEquals("⛈️", WeatherCodeMapper.getEmoji(95))
    }
}
