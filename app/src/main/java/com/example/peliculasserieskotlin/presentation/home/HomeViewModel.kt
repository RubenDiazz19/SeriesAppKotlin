package com.example.peliculasserieskotlin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.domain.Movie
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            delay(1000) // Simula llamada a la API
            val fakeMovies = listOf(
                Movie(1, "Regreso al Futuro", "1985", "https://via.placeholder.com/150"),
                Movie(2, "El Padrino", "1972", "https://via.placeholder.com/150"),
                Movie(3, "Star Wars", "1977", "https://via.placeholder.com/150")
            )
            _uiState.value = HomeUiState(
                isLoading = false,
                movies = fakeMovies
            )
        }
    }
}
