package com.example.peliculasserieskotlin.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.features.shared.repository.MediaRepository
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.peliculasserieskotlin.core.model.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MediaDetailUiState?>(null)
    val uiState: StateFlow<MediaDetailUiState?> = _uiState.asStateFlow()

    fun loadDetail(id: Int, type: MediaType) {
        viewModelScope.launch {
            val detailItem = when (type) {
                MediaType.MOVIE  -> mediaRepository.getMovieDetails(id)
                //TODO(): Implementar getSeriesDetails
                MediaType.SERIES -> mediaRepository.getSeriesDetails(id)   // ← temporal
            }

            _uiState.value = detailItem?.let { item ->
                MediaDetailUiState(
                    title                = item.title,
                    tagline              = item.tagline,
                    overview             = item.overview,
                    posterUrl            = item.posterUrl,
                    originalTitle        = item.originalTitle,
                    releaseDate          = formatDate(item.releaseDate),
                    voteAverageFormatted = "${item.voteAverage} / 10\n(${item.voteCount} votos)",
                    runtimeFormatted     = item.runtime?.let { "$it minutos" },
                    budgetFormatted      = item.budget?.let  { "$${"%,d".format(it)}" },
                    revenueFormatted     = item.revenue?.let { "$${"%,d".format(it)}" },
                    status               = item.status,
                    genres               = item.genres
                )
            }
        }
    }

    // Funciones para manejar favoritos
    fun toggleFavorite(item: MediaItem, markedAsFavorite: Boolean) = viewModelScope.launch {
        if (markedAsFavorite)
            favoriteRepository.addFavorite(item)
        else
            favoriteRepository.removeFavorite(item.id, item.type)
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

    // Función para saber si un elemento está en favoritos
    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> = favoriteRepository.isFavorite(id, type)
}