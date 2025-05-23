package com.example.peliculasserieskotlin.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    /*####################################################################*/
    /* -----------------------  ACCIONES  ------------------------------- */
    /*####################################################################*/

    fun toggleFavorite(item: MediaItem, markedAsFavorite: Boolean) = viewModelScope.launch {
        if (markedAsFavorite)
            favoriteRepository.addFavorite(item)
        else
            favoriteRepository.removeFavorite(item.id, item.type)
    }

    /*####################################################################*/
    /* -----------------------  LECTURAS  ------------------------------- */
    /*####################################################################*/

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        favoriteRepository.isFavorite(id, type)

    fun favoritesOf(type: MediaType): Flow<List<MediaItem>> =
        favoriteRepository.getFavoriteMedia(type)
}