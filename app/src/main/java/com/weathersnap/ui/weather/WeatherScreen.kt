package com.weathersnap.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weathersnap.ui.components.ErrorMessage
import com.weathersnap.ui.components.GradientHeader
import com.weathersnap.ui.components.LoadingIndicator
import com.weathersnap.ui.components.WeatherCard
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.theme.WeatherSnapTheme
import com.weathersnap.ui.theme.WeatherSnapTypography

@Composable
fun WeatherScreen(
    onCreateReport: (Weather) -> Unit,
    onViewReports: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = WeatherSnapTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Header
        item {
            GradientHeader(
                title = "WeatherSnap",
                subtitle = "Live weather reports with camera evidence"
            ) {
                Button(
                    onClick = onViewReports,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.card,
                        contentColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Reports", style = WeatherSnapTypography.labelLarge)
                }
            }
        }

        // Search section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChange,
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.cardBorder,
                            cursorColor = colors.primary,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.textSecondary,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    // Search button with prominent olive fill (matching PDF)
                    Button(
                        onClick = viewModel::onSearchClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            "Search",
                            style = WeatherSnapTypography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter more than 2 letters to start city suggestions.",
                    style = WeatherSnapTypography.bodySmall,
                    color = colors.textTertiary
                )
            }
        }

        // City suggestions with animation
        if (uiState.showSuggestions && uiState.suggestions.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                    exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.card)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                    ) {
                        uiState.suggestions.forEachIndexed { index, city ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(tween(200, delayMillis = index * 50)) { -it } +
                                        fadeIn(tween(200, delayMillis = index * 50))
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onCitySelected(city) }
                                            .padding(16.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = city.name,
                                                style = WeatherSnapTypography.titleMedium,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = "${city.admin1 ?: ""} ${city.country}".trim(),
                                                style = WeatherSnapTypography.bodySmall,
                                                color = colors.textTertiary
                                            )
                                        }
                                    }
                                    if (index < uiState.suggestions.lastIndex) {
                                        HorizontalDivider(color = colors.cardBorder)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Loading state for suggestions
        if (uiState.suggestionsLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colors.primary,
                        strokeWidth = 3.dp
                    )
                }
            }
        }

        // Error
        if (uiState.error != null) {
            item {
                ErrorMessage(message = uiState.error!!)
            }
        }

        // Weather loading
        if (uiState.weatherLoading) {
            item {
                LoadingIndicator(message = "Fetching weather data...")
            }
        }

        // Weather result
        if (uiState.weather != null && !uiState.weatherLoading) {
            item {
                WeatherCard(weather = uiState.weather!!)
            }

            // Report readiness + Create Report
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Report readiness",
                            style = WeatherSnapTypography.bodyMedium,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "Camera and Room DB enabled",
                            style = WeatherSnapTypography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { uiState.weather?.let { onCreateReport(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Create Report",
                            style = WeatherSnapTypography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Default state — "Search. Capture. Save." banner + instructions (matching PDF)
        if (uiState.weather == null && !uiState.weatherLoading && !uiState.suggestionsLoading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                ) {
                    // Gradient banner — "Search. Capture. Save."
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        colors.headerGradientStart,
                                        Color(0xFF2A5A4A),
                                        colors.headerGradientEnd
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search. Capture. Save.",
                            style = WeatherSnapTypography.titleLarge,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                    }

                    // "No weather loaded" text
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No weather loaded",
                            style = WeatherSnapTypography.titleMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter more than 2 letters, choose a city, then search.",
                            style = WeatherSnapTypography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Empty state (no cities found for search)
        if (uiState.isEmpty && !uiState.suggestionsLoading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No cities found for your search.",
                        style = WeatherSnapTypography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
