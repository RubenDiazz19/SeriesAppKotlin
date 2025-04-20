package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import com.example.peliculasserieskotlin.data.repository.SeriesRepository
import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.domain.model.Series
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    // Nuevo estado para el buscador inline
    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    fun forceShowSearchBar() {
        Log.d("FAB", "forceShowSearchBar() llamado")
        _showSearchBarForced.value = true
    }

    fun resetShowSearchBar() {
        Log.d("FAB", "resetShowSearchBar() llamado")
        _showSearchBarForced.value = false
    }

    fun showInlineSearch() {
        Log.d("FAB", "showInlineSearch() llamado")
        _inlineSearchActive.value = true
    }

    fun hideInlineSearch() {
        Log.d("FAB", "hideInlineSearch() llamado")
        _inlineSearchActive.value = false
    }

    var searchText by mutableStateOf("")
        private set

    var isSearchingRemotely by mutableStateOf(false)
        private set

    private val _filteredMovies = mutableStateOf<List<Movie>>(emptyList())
    val filteredMovies: State<List<Movie>> = _filteredMovies

    private val _filteredSeries = mutableStateOf<List<Series>>(emptyList())
    val filteredSeries: State<List<Series>> = _filteredSeries

    private var searchJob: Job? = null

    init {
        loadMovies()
        loadSeries()

        // Capturar eventos de tecla "back" cuando el buscador está activo
        setupBackHandler()
    }

    private fun setupBackHandler() {
        viewModelScope.launch {
            // Cuando cambia el estado del buscador inline, manejamos la tecla back
            inlineSearchActive.collect { isActive ->
                if (isActive) {
                    // La lógica para detectar la tecla back se maneja a nivel de actividad
                    // Esto es solo un placeholder para la funcionalidad
                    // En una implementación real, configurarías un OnBackPressedCallback en la actividad
                }
            }
        }
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun loadMovies() {
        if (!canPaginateMovies) return
        viewModelScope.launch {
            movieRepository.getMovies(currentMoviePage, genre = null).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _movies.update { it + newMovies }
                }
            }
        }
    }

    private fun loadSeries() {
        if (!canPaginateSeries) return
        viewModelScope.launch {
            seriesRepository.getSeries(currentSeriesPage, genre = null).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _series.update { it + newSeries }
                }
            }
        }
    }

    fun loadNextPage() {
        if (selectedCategory.value == "Películas" && canPaginateMovies) {
            currentMoviePage++
            loadMovies()
        } else if (canPaginateSeries) {
            currentSeriesPage++
            loadSeries()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchText = query

        val localMovies = movies.value.filter {
            it.title.contains(query, ignoreCase = true)
        }
        val localSeries = series.value.filter {
            it.name.contains(query, ignoreCase = true)
        }

        _filteredMovies.value = localMovies
        _filteredSeries.value = localSeries

        searchJob?.cancel()

        if (localMovies.isNotEmpty() || localSeries.isNotEmpty()) return

        searchJob = viewModelScope.launch {
            delay(500)
            searchRemotely(query)
        }
    }

    private fun searchRemotely(query: String) {
        viewModelScope.launch {
            isSearchingRemotely = true
            val remoteMovies = movieRepository.searchMovies(query)
            val remoteSeries = seriesRepository.searchSeries(query)
            _filteredMovies.value = remoteMovies
            _filteredSeries.value = remoteSeries
            isSearchingRemotely = false
        }
    }
}