package com.weathersnap.ui.saved

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weathersnap.domain.model.Report
import com.weathersnap.ui.components.EmptyState
import com.weathersnap.ui.components.GradientHeader
import com.weathersnap.ui.report.SizeChip
import com.weathersnap.ui.report.formatFileSize
import com.weathersnap.ui.theme.WeatherSnapTheme
import com.weathersnap.ui.theme.WeatherSnapTypography
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedReportsScreen(
    onBack: () -> Unit,
    viewModel: SavedReportsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsStateWithLifecycle(initialValue = emptyList())
    val reportCount by viewModel.reportCount.collectAsStateWithLifecycle(initialValue = 0)
    val colors = WeatherSnapTheme.colors

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
                title = "Saved Reports",
                subtitle = if (reportCount > 0) "$reportCount report${if (reportCount > 1) "s" else ""} stored locally"
                else "No reports yet"
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

        // Empty state
        if (reports.isEmpty()) {
            item {
                EmptyState(message = "No reports saved yet.\nCreate your first weather report!")
            }
        }

        // Report cards
        itemsIndexed(reports, key = { _, report -> report.id }) { index, report ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(report.id) {
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = index * 100)) +
                        slideInVertically(tween(400, delayMillis = index * 100)) { it / 4 }
            ) {
                ReportCard(report = report)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun ReportCard(report: Report) {
    val colors = WeatherSnapTheme.colors
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Captured image
        if (report.imagePath.isNotEmpty()) {
            AsyncImage(
                model = File(report.imagePath),
                contentDescription = "Report photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // City, condition, temperature
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${report.cityName}, ${report.country}",
                    style = WeatherSnapTypography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.condition,
                    style = WeatherSnapTypography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = dateFormat.format(Date(report.timestamp)),
                    style = WeatherSnapTypography.bodySmall,
                    color = colors.textTertiary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.primary.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${report.temperature.toInt()}°C",
                    style = WeatherSnapTypography.headlineMedium,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image sizes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SizeChip(
                label = "Original",
                size = formatFileSize(report.originalSizeBytes),
                color = colors.humidityValue,
                bgColor = colors.humidityCardBg,
                modifier = Modifier.weight(1f)
            )
            SizeChip(
                label = "Compressed",
                size = formatFileSize(report.compressedSizeBytes),
                color = colors.windValue,
                bgColor = colors.windCardBg,
                modifier = Modifier.weight(1f)
            )
        }

        // Notes
        if (report.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.background.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = report.notes,
                    style = WeatherSnapTypography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
