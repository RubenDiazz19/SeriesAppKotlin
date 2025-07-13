package com.example.peliculasserieskotlin.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.core.model.Serie
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    fun toggleFavorite(item: Serie, markedAsFavorite: Boolean) = viewModelScope.launch {
        if (markedAsFavorite)
            favoriteRepository.addFavorite(item)
        else
            favoriteRepository.removeFavorite(item.id)
    }

    fun isFavorite(id: Int): Flow<Boolean> =
        favoriteRepository.isFavorite(id)

    fun favoriteSeries(): Flow<List<Serie>> =
        favoriteRepository.getFavoriteSeries()
}