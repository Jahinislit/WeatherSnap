package com.weathersnap.ui.report

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.repository.ReportRepository
import com.weathersnap.domain.model.Report
import com.weathersnap.domain.model.Weather
import com.weathersnap.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class CreateReportUiState(
    val weather: Weather? = null,
    val originalImagePath: String? = null,
    val compressedImagePath: String? = null,
    val originalSizeBytes: Long = 0,
    val compressedSizeBytes: Long = 0,
    val notes: String = "",
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null,
    val draftId: Long? = null
)

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val imageCompressor: ImageCompressor,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    init {
        // Restore state from SavedStateHandle (survives process death)
        val savedNotes = savedStateHandle.get<String>("notes") ?: ""
        val savedImagePath = savedStateHandle.get<String>("compressedImagePath")
        val savedOriginalPath = savedStateHandle.get<String>("originalImagePath")
        val savedOriginalSize = savedStateHandle.get<Long>("originalSize") ?: 0L
        val savedCompressedSize = savedStateHandle.get<Long>("compressedSize") ?: 0L

        if (savedImagePath != null || savedNotes.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    notes = savedNotes,
                    compressedImagePath = savedImagePath,
                    originalImagePath = savedOriginalPath,
                    originalSizeBytes = savedOriginalSize,
                    compressedSizeBytes = savedCompressedSize
                )
            }
        }
    }

    fun setWeather(weather: Weather) {
        _uiState.update { it.copy(weather = weather) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
        savedStateHandle["notes"] = notes
    }

    fun onImageCaptured(originalPath: String) {
        viewModelScope.launch {
            try {
                val result = imageCompressor.compressImage(context, originalPath)
                _uiState.update {
                    it.copy(
                        originalImagePath = originalPath,
                        compressedImagePath = result.compressedPath,
                        originalSizeBytes = result.originalSizeBytes,
                        compressedSizeBytes = result.compressedSizeBytes
                    )
                }
                // Save to SavedStateHandle for lifecycle survival
                savedStateHandle["originalImagePath"] = originalPath
                savedStateHandle["compressedImagePath"] = result.compressedPath
                savedStateHandle["originalSize"] = result.originalSizeBytes
                savedStateHandle["compressedSize"] = result.compressedSizeBytes
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to compress image: ${e.message}")
                }
            }
        }
    }

    fun saveReport() {
        val state = _uiState.value
        val weather = state.weather ?: return
        val imagePath = state.compressedImagePath ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val report = Report(
                    cityName = weather.cityName,
                    country = weather.country,
                    temperature = weather.temperature,
                    condition = weather.condition,
                    humidity = weather.humidity,
                    windSpeed = weather.windSpeed,
                    pressure = weather.pressure,
                    imagePath = imagePath,
                    originalSizeBytes = state.originalSizeBytes,
                    compressedSizeBytes = state.compressedSizeBytes,
                    notes = state.notes,
                    timestamp = System.currentTimeMillis(),
                    isDraft = false
                )
                reportRepository.saveReport(report)

                // Clean up original image (keep only compressed)
                state.originalImagePath?.let { path ->
                    val file = File(path)
                    if (file.exists() && path != imagePath) file.delete()
                }

                // Clear saved state
                savedStateHandle.remove<String>("notes")
                savedStateHandle.remove<String>("compressedImagePath")
                savedStateHandle.remove<String>("originalImagePath")
                savedStateHandle.remove<Long>("originalSize")
                savedStateHandle.remove<Long>("compressedSize")

                _uiState.update {
                    it.copy(isSaving = false, savedSuccessfully = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save report: ${e.message}"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up temp files if report was not saved
        val state = _uiState.value
        if (!state.savedSuccessfully) {
            state.originalImagePath?.let { path ->
                File(path).takeIf { it.exists() }?.delete()
            }
            state.compressedImagePath?.let { path ->
                File(path).takeIf { it.exists() }?.delete()
            }
        }
    }
}
