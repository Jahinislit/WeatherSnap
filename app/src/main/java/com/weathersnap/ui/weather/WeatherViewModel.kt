package com.weathersnap.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import com.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val query: String = "",
    val suggestions: List<City> = emptyList(),
    val showSuggestions: Boolean = false,
    val suggestionsLoading: Boolean = false,
    val weather: Weather? = null,
    val weatherLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        // Debounce city search input
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collect { query ->
                    searchCities(query)
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                showSuggestions = query.length > 2,
                error = null
            )
        }
        _searchQuery.value = query

        if (query.length <= 2) {
            _uiState.update {
                it.copy(
                    suggestions = emptyList(),
                    showSuggestions = false,
                    suggestionsLoading = false
                )
            }
        }
    }

    private fun searchCities(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(suggestionsLoading = true) }

            weatherRepository.searchCities(query).fold(
                onSuccess = { cities ->
                    _uiState.update {
                        it.copy(
                            suggestions = cities,
                            suggestionsLoading = false,
                            showSuggestions = cities.isNotEmpty(),
                            isEmpty = cities.isEmpty()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            suggestionsLoading = false,
                            error = error.message ?: "Failed to search cities"
                        )
                    }
                }
            )
        }
    }

    fun onCitySelected(city: City) {
        _uiState.update {
            it.copy(
                query = city.displayName,
                showSuggestions = false,
                suggestions = emptyList(),
                weatherLoading = true,
                error = null,
                isEmpty = false
            )
        }

        viewModelScope.launch {
            weatherRepository.getWeather(city).fold(
                onSuccess = { weather ->
                    _uiState.update {
                        it.copy(
                            weather = weather,
                            weatherLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            weatherLoading = false,
                            error = error.message ?: "Failed to fetch weather"
                        )
                    }
                }
            )
        }
    }

    fun onSearchClick() {
        val query = _uiState.value.query
        if (query.length > 2) {
            _uiState.update { it.copy(showSuggestions = false) }
            searchCities(query)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
