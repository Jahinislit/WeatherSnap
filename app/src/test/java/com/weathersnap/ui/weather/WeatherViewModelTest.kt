package com.weathersnap.ui.weather

import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherRepository: WeatherRepository
    private val testDispatcher = StandardTestDispatcher()

    private val testCity = City(
        name = "London",
        country = "UK",
        latitude = 51.5074,
        longitude = -0.1278,
        admin1 = "England"
    )

    private val testWeather = Weather(
        cityName = "London",
        country = "UK",
        temperature = 15.0,
        conditionCode = 2,
        condition = "Partly cloudy",
        humidity = 65,
        windSpeed = 5.0,
        pressure = 1013.0,
        latitude = 51.5074,
        longitude = -0.1278
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherRepository = mockk()
        viewModel = WeatherViewModel(weatherRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query and no weather`() {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertNull(state.weather)
        assertFalse(state.weatherLoading)
        assertNull(state.error)
    }

    @Test
    fun `onQueryChange updates query in state`() {
        viewModel.onQueryChange("Lon")
        assertEquals("Lon", viewModel.uiState.value.query)
    }

    @Test
    fun `query with 2 or fewer letters does not show suggestions`() {
        viewModel.onQueryChange("Lo")
        assertFalse(viewModel.uiState.value.showSuggestions)
    }

    @Test
    fun `query with more than 2 letters enables suggestion visibility`() {
        viewModel.onQueryChange("Lon")
        assertTrue(viewModel.uiState.value.showSuggestions)
    }

    @Test
    fun `onCitySelected fetches weather and updates state`() = runTest {
        coEvery { weatherRepository.getWeather(testCity) } returns Result.success(testWeather)

        viewModel.onCitySelected(testCity)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.weather)
        assertEquals("London", state.weather?.cityName)
        assertEquals(15.0, state.weather?.temperature ?: 0.0, 0.01)
        assertFalse(state.weatherLoading)
        assertFalse(state.showSuggestions)
    }

    @Test
    fun `onCitySelected handles error`() = runTest {
        coEvery { weatherRepository.getWeather(testCity) } returns Result.failure(Exception("Network error"))

        viewModel.onCitySelected(testCity)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.weather)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Network error"))
    }
}
