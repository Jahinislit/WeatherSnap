package com.weathersnap.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.components.WeatherCard
import com.weathersnap.ui.theme.WeatherSnapTheme
import org.junit.Rule
import org.junit.Test

class WeatherCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

    @Test
    fun weatherCard_displaysCity() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("London, UK").assertIsDisplayed()
    }

    @Test
    fun weatherCard_displaysCondition() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("Partly cloudy").assertIsDisplayed()
    }

    @Test
    fun weatherCard_displaysTemperature() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("15°C").assertIsDisplayed()
    }

    @Test
    fun weatherCard_displaysHumidity() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
    }

    @Test
    fun weatherCard_displaysWindSpeed() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("5.0 m/s").assertIsDisplayed()
    }

    @Test
    fun weatherCard_displaysPressure() {
        composeTestRule.setContent {
            WeatherSnapTheme {
                WeatherCard(weather = testWeather)
            }
        }
        composeTestRule.onNodeWithText("1013").assertIsDisplayed()
    }
}
