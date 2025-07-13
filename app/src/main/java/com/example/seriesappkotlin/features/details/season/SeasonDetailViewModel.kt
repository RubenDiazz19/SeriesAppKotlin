package com.example.seriesappkotlin.features.details.season

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesappkotlin.features.shared.repository.SerieRepository
import com.example.seriesappkotlin.core.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonDetailViewModel @Inject constructor(
    private val serieRepository: SerieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeasonDetailUiState?>(null)
    val uiState: StateFlow<SeasonDetailUiState?> = _uiState.asStateFlow()

    fun loadSeasonDetails(serieId: Int, seasonNumber: Int) {
        viewModelScope.launch {
            val result = serieRepository.getSeasonDetails(serieId, seasonNumber)

            _uiState.value = when (result) {
                is AppResult.Success -> {
                    val season = result.data
                    SeasonDetailUiState(
                        name = season.name,
                        overview = season.overview,
                        posterUrl = season.posterUrl, // Corregido: era season.posterUrl
                        episodes = season.episodes,
                        seasonNumber = season.seasonNumber,
                        airDate = null // El modelo Season no tiene airDate
                    )
                }
                is AppResult.Error -> {
                    SeasonDetailUiState(error = result.exception.localizedMessage ?: "Error desconocido")
                }
            }
        }
    }
}