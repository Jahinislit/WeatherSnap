package com.weathersnap.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.components.ErrorMessage
import com.weathersnap.ui.components.GradientHeader
import com.weathersnap.ui.components.LoadingIndicator
import com.weathersnap.ui.components.WeatherCard
import com.weathersnap.ui.theme.WeatherSnapTheme
import com.weathersnap.ui.theme.WeatherSnapTypography
import java.io.File

@Composable
fun CreateReportScreen(
    weather: Weather?,
    capturedImagePath: String?,
    onBack: () -> Unit,
    onCapturePhoto: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = WeatherSnapTheme.colors

    // Set weather data
    LaunchedEffect(weather) {
        weather?.let { viewModel.setWeather(it) }
    }

    // Handle captured image from camera
    LaunchedEffect(capturedImagePath) {
        capturedImagePath?.let { viewModel.onImageCaptured(it) }
    }

    // Navigate on successful save
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            onSaved()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header
        item {
            GradientHeader(
                title = "Create Report",
                subtitle = "Capture, compress, annotate"
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.card,
                        contentColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Back", style = WeatherSnapTypography.labelLarge)
                }
            }
        }

        // Weather snapshot
        if (uiState.weather != null) {
            item {
                WeatherCard(weather = uiState.weather!!, compact = true)
            }
        }

        // Photo section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                // Image preview
                val imagePath = uiState.compressedImagePath

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.card.copy(alpha = 0.5f))
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imagePath != null) {
                        AsyncImage(
                            model = File(imagePath),
                            contentDescription = "Captured photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Photo preview",
                            style = WeatherSnapTypography.bodyLarge,
                            color = colors.textTertiary
                        )
                    }
                }

                // Image sizes
                if (uiState.originalSizeBytes > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SizeChip(
                            label = "Original",
                            size = formatFileSize(uiState.originalSizeBytes),
                            color = colors.humidityValue,
                            bgColor = colors.humidityCardBg,
                            modifier = Modifier.weight(1f)
                        )
                        SizeChip(
                            label = "Compressed",
                            size = formatFileSize(uiState.compressedSizeBytes),
                            color = colors.windValue,
                            bgColor = colors.windCardBg,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Capture button
                Button(
                    onClick = onCapturePhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Capture Photo",
                        style = WeatherSnapTypography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Notes section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Field Notes",
                    style = WeatherSnapTypography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
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
            }
        }

        // Error
        if (uiState.error != null) {
            item {
                ErrorMessage(message = uiState.error!!)
            }
        }

        // Save button
        item {
            if (uiState.isSaving) {
                LoadingIndicator(message = "Saving report...")
            } else {
                Button(
                    onClick = viewModel::saveReport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = uiState.compressedImagePath != null && uiState.weather != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.Black,
                        disabledContainerColor = colors.primary.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Save Report",
                        style = WeatherSnapTypography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun SizeChip(
    label: String,
    size: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = WeatherSnapTypography.labelMedium,
            color = WeatherSnapTheme.colors.textSecondary
        )
        Text(
            text = size,
            style = WeatherSnapTypography.titleMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
