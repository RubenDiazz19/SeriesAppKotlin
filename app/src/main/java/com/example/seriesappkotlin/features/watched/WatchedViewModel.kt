package com.example.seriesappkotlin.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.shared.repository.WatchedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchedViewModel @Inject constructor(
    private val watchedRepository: WatchedRepository
) : ViewModel() {

    fun toggleWatched(item: Serie, markedAsWatched: Boolean) = viewModelScope.launch {
        if (markedAsWatched)
            watchedRepository.addWatched(item)
        else
            watchedRepository.removeWatched(item.id)
    }

    fun isWatched(id: Int): Flow<Boolean> =
        watchedRepository.isWatched(id)

    fun watchedSeries(): Flow<List<Serie>> =
        watchedRepository.getWatchedSeries()
}