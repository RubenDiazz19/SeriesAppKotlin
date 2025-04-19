package com.example.peliculasserieskotlin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import com.example.peliculasserieskotlin.data.repository.SeriesRepository
import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.domain.model.Series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _series = MutableStateFlow<List<Series>>(emptyList())
    val series: StateFlow<List<Series>> = _series.asStateFlow()

    private var currentMoviePage = 1
    private var currentSeriesPage = 1
    private var canPaginateMovies = true
    private var canPaginateSeries = true

    init {
        loadMovies()
        loadSeries()
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun loadMovies() {
        viewModelScope.launch {
            movieRepository.getMovies(currentMoviePage, genre = null).collect { newMovies ->
                if (newMovies.isEmpty()) canPaginateMovies = false
                _movies.update { it + newMovies }
            }
        }
    }

    private fun loadSeries() {
        viewModelScope.launch {
            seriesRepository.getSeries(currentSeriesPage, genre = null).collect { newSeries ->
                if (newSeries.isEmpty()) canPaginateSeries = false
                _series.update { it + newSeries }
            }
        }
    }

    fun loadNextPage() {
        if (selectedCategory.value == "Películas" && canPaginateMovies) {
            currentMoviePage++
            loadMovies()
        } else if (selectedCategory.value == "Series" && canPaginateSeries) {
            currentSeriesPage++
            loadSeries()
        }
    }

}
