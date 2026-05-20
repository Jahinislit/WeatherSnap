package com.weathersnap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.theme.WeatherSnapTheme
import com.weathersnap.ui.theme.WeatherSnapTypography

@Composable
fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val colors = WeatherSnapTheme.colors
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(weather) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${weather.cityName}, ${weather.country}",
                        style = WeatherSnapTypography.headlineMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = weather.condition,
                        style = WeatherSnapTypography.bodyLarge,
                        color = colors.textSecondary
                    )
                }
                // Temperature badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${weather.temperature.toInt()}°C",
                        style = WeatherSnapTypography.temperature.copy(
                            fontSize = if (compact) 24.sp else 36.sp
                        ),
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metric cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    backgroundColor = colors.humidityCardBg,
                    valueColor = colors.humidityValue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Wind",
                    value = "${weather.windSpeed} m/s",
                    backgroundColor = colors.windCardBg,
                    valueColor = colors.windValue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Pressure",
                    value = "${weather.pressure.toInt()}",
                    backgroundColor = colors.pressureCardBg,
                    valueColor = colors.pressureValue,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    backgroundColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = WeatherSnapTypography.labelMedium,
            color = WeatherSnapTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = WeatherSnapTypography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun GradientHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    val colors = WeatherSnapTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        colors.headerGradientStart,
                        colors.headerGradientCenter,
                        colors.headerGradientEnd
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = WeatherSnapTypography.headlineLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = WeatherSnapTypography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
            action?.invoke()
        }
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = WeatherSnapTheme.colors.primary,
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = WeatherSnapTypography.bodyMedium,
            color = WeatherSnapTheme.colors.textSecondary
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WeatherSnapTheme.colors.error.copy(alpha = 0.15f))
            .border(1.dp, WeatherSnapTheme.colors.error.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = WeatherSnapTypography.bodyMedium,
            color = WeatherSnapTheme.colors.error
        )
    }
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = WeatherSnapTypography.bodyLarge,
            color = WeatherSnapTheme.colors.textTertiary
        )
    }
}
