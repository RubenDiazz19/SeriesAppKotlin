package com.example.peliculasserieskotlin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.data.repository.SeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 1
    private var canPaginate = true

    init {
        loadSeries()
    }

    fun loadNextPage() {
        if (canPaginate && !_uiState.value.isLoading) {
            currentPage++
            loadSeries()
        }
    }

    private fun loadSeries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            seriesRepository.getSeries(currentPage, genre = null).collect { newSeries ->
                if (newSeries.isEmpty()) canPaginate = false

                _uiState.update { currentState ->
                    currentState.copy(
                        series = currentState.series + newSeries,
                        isLoading = false
                    )
                }
            }
        }
    }
}
