package com.weathersnap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OliveYellow,
    onPrimary = Color.Black,
    primaryContainer = OliveYellowMuted,
    secondary = OliveYellowDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    outline = DarkCardBorder
)

@Immutable
data class WeatherSnapColors(
    val background: Color = DarkBackground,
    val surface: Color = DarkSurface,
    val card: Color = DarkCard,
    val cardBorder: Color = DarkCardBorder,
    val primary: Color = OliveYellow,
    val primaryDark: Color = OliveYellowDark,
    val headerGradientStart: Color = HeaderGradientStart,
    val headerGradientCenter: Color = HeaderGradientCenter,
    val headerGradientEnd: Color = HeaderGradientEnd,
    val humidityValue: Color = HumidityGreen,
    val windValue: Color = WindTeal,
    val pressureValue: Color = PressureGold,
    val humidityCardBg: Color = HumidityCardBg,
    val windCardBg: Color = WindCardBg,
    val pressureCardBg: Color = PressureCardBg,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
    val error: Color = ErrorRed,
    val success: Color = SuccessGreen
)

val LocalWeatherSnapColors = staticCompositionLocalOf { WeatherSnapColors() }

object WeatherSnapTheme {
    val colors: WeatherSnapColors
        @Composable
        get() = LocalWeatherSnapColors.current

    val typography: WeatherSnapTypography
        get() = WeatherSnapTypography
}

@Composable
fun WeatherSnapTheme(
    content: @Composable () -> Unit
) {
    val weatherSnapColors = WeatherSnapColors()

    CompositionLocalProvider(
        LocalWeatherSnapColors provides weatherSnapColors
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}
