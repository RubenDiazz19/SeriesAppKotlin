package com.example.peliculasserieskotlin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.domain.MovieRepository
import com.example.peliculasserieskotlin.domain.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getMovies().collect { movies ->
                _uiState.value = HomeUiState(isLoading = false, movies = movies)
            }
        }
    }

    fun insertInitialMovies() {
        viewModelScope.launch {
            val initialMovies = listOf(
                Movie(1, "Regreso al Futuro", "1985", "https://via.placeholder.com/150"),
                Movie(2, "El Padrino", "1972", "https://via.placeholder.com/150"),
                Movie(3, "Star Wars", "1977", "https://via.placeholder.com/150")
            )
            repository.insertMovies(initialMovies)
        }
    }
}
