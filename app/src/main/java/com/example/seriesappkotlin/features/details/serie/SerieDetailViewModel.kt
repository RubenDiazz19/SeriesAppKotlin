package com.example.seriesappkotlin.features.details.serie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.shared.repository.SerieRepository
import com.example.seriesappkotlin.features.shared.repository.WatchedRepository
import com.example.seriesappkotlin.core.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SerieDetailViewModel @Inject constructor(
    private val serieRepository: SerieRepository,
    private val watchedRepository: WatchedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SerieDetailUiState?>(null)
    val uiState: StateFlow<SerieDetailUiState?> = _uiState.asStateFlow()

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            try {
                // Asumimos que getSerieDetails ahora devuelve AppResult<Serie>
                val result = serieRepository.getSerieDetails(id)

                _uiState.value = when (result) {
                    is AppResult.Success -> {
                        val item = result.data
                        SerieDetailUiState(
                            title = item.title,
                            tagline = item.tagline,
                            overview = item.overview,
                            posterUrl = item.posterUrl,
                            originalTitle = item.originalTitle,
                            releaseDate = formatDate(item.firstAirDate),
                            voteAverageFormatted = "${item.voteAverage} / 10",
                            runtimeFormatted = item.runtime?.let { "$it minutos" },
                            status = item.status,
                            genres = item.genres,
                            numberOfSeasons = item.numberOfSeasons,
                            numberOfEpisodes = item.numberOfEpisodes,
                            seasons = item.seasons,
                            error = null
                        )
                    }
                    is AppResult.Error -> {
                        SerieDetailUiState(error = result.exception.localizedMessage ?: "Error desconocido")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SerieDetailUiState(error = e.localizedMessage ?: "Error al cargar los detalles")
            }
        }
    }

    // Funciones para manejar vistos
    fun toggleWatched(item: Serie, markedAsWatched: Boolean) = viewModelScope.launch {
        try {
            if (markedAsWatched)
                watchedRepository.addWatched(item)
            else
                watchedRepository.removeWatched(item.id)
        } catch (e: Exception) {
            // Manejar error de vistos si es necesario
        }
    }

    // Función helper para formatear la fecha a dd/mm/yyyy
    fun formatDate(dateString: String?): String? {
        return dateString?.let { date ->
            try {
                // Asumiendo que viene en formato yyyy-mm-dd
                val parts = date.split("-")
                if (parts.size == 3) {
                    "${parts[2]}/${parts[1]}/${parts[0]}"
                } else {
                    date // Devolver original si no tiene el formato esperado
                }
            } catch (e: Exception) {
                date // Devolver original en caso de error
            }
        }
    }

    // Función para saber si un elemento está en vistos
    fun isWatched(id: Int): Flow<Boolean> = watchedRepository.isWatched(id)
}