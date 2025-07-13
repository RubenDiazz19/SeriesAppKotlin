package com.example.seriesappkotlin.features.details.season

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesappkotlin.features.shared.repository.SerieRepository
import com.example.seriesappkotlin.features.shared.repository.WatchedRepository
import com.example.seriesappkotlin.core.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonDetailViewModel @Inject constructor(
    private val serieRepository: SerieRepository,
    private val watchedRepository: WatchedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeasonDetailUiState?>(null)
    val uiState: StateFlow<SeasonDetailUiState?> = _uiState.asStateFlow()

    fun loadSeasonDetails(serieId: Int, seasonNumber: Int) {
        viewModelScope.launch {
            val result = serieRepository.getSeasonDetails(serieId, seasonNumber)

            _uiState.value = when (result) {
                is AppResult.Success -> {
                    val season = result.data
                    
                    // Cargar el estado de episodios vistos desde la base de datos
                    val watchedEpisodes = watchedRepository.getWatchedEpisodesForSeason(serieId, seasonNumber).first()
                    
                    // Actualizar el estado isWatched de cada episodio
                    season.episodes?.forEach { episode ->
                        episode.isWatched = watchedEpisodes.any { 
                            it.episodeNumber == episode.episodeNumber 
                        }
                    }
                    
                    SeasonDetailUiState(
                        name = season.name,
                        overview = season.overview,
                        posterUrl = season.posterUrl,
                        episodes = season.episodes,
                        seasonNumber = season.seasonNumber,
                        airDate = null
                    )
                }
                is AppResult.Error -> {
                    SeasonDetailUiState(error = result.exception.localizedMessage ?: "Error desconocido")
                }
            }
        }
    }
}