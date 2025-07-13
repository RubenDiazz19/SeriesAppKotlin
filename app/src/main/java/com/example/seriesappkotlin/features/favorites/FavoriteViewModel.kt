package com.example.seriesappkotlin.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.shared.repository.FavoriteRepository
import com.example.seriesappkotlin.features.shared.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun toggleFavorite(item: Serie, markedAsFavorite: Boolean) = viewModelScope.launch {
        if (markedAsFavorite)
            favoriteRepository.addFavorite(item)
        else
            favoriteRepository.removeFavorite(item.id)
    }

    fun isFavorite(id: Int): Flow<Boolean> =
        favoriteRepository.isFavorite(id)
        
    fun isUserLoggedIn(): Boolean =
        userRepository.isUserLoggedIn()

    fun favoriteSeries(): Flow<List<Serie>> =
        favoriteRepository.getFavoriteSeries()

}

