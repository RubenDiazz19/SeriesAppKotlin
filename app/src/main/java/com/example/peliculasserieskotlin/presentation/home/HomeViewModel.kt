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

    var isLoadingData by mutableStateOf(false)
        private set

    private val _filteredMovies = mutableStateOf<List<Movie>>(emptyList())
    val filteredMovies: State<List<Movie>> = _filteredMovies

    private val _filteredSeries = mutableStateOf<List<Series>>(emptyList())
    val filteredSeries: State<List<Series>> = _filteredSeries

    private var searchJob: Job? = null

    enum class SortType { ALPHABETIC, RATING, FAVORITE }

    private val _sortBy = MutableStateFlow(SortType.RATING)
    val sortBy: StateFlow<SortType> = _sortBy.asStateFlow()

    init {
        loadInitialData()
        setupBackHandler()
    }

    private fun loadInitialData() {
        // Cargar datos según el tipo de ordenación inicial
        when (_sortBy.value) {
            SortType.ALPHABETIC -> {
                loadMovies()
                loadSeries()
            }
            SortType.RATING -> {
                loadTopRatedMovies()
                loadTopRatedSeries()
            }
            SortType.FAVORITE -> {
                loadFavoriteMovies()
                loadFavoriteSeries()
            }
        }
    }

    private fun setupBackHandler() {
        viewModelScope.launch {
            inlineSearchActive.collect { isActive ->
                if (isActive) {
                    // La lógica para detectar la tecla back se maneja a nivel de actividad
                }
            }
        }
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun loadMovies() {
        if (!canPaginateMovies) return
        isLoadingData = true
        viewModelScope.launch {
            movieRepository.getMovies(currentMoviePage, genre = null).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _movies.update { it + newMovies }
                }
                isLoadingData = false
            }
        }
    }

    private fun loadTopRatedMovies() {
        if (!canPaginateMovies) return
        isLoadingData = true
        viewModelScope.launch {
            movieRepository.getTopRatedMovies(currentMoviePage).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _movies.update { it + newMovies }
                }
                isLoadingData = false
            }
        }
    }

    private fun loadFavoriteMovies() {
        if (!canPaginateMovies) return
        isLoadingData = true
        viewModelScope.launch {
            movieRepository.getFavoriteMovies(currentMoviePage).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _movies.update { it + newMovies }
                }
                isLoadingData = false
            }
        }
    }

    private fun loadSeries() {
        if (!canPaginateSeries) return
        isLoadingData = true
        viewModelScope.launch {
            seriesRepository.getSeries(currentSeriesPage, genre = null).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _series.update { it + newSeries }
                }
                isLoadingData = false
            }
        }
    }

    private fun loadTopRatedSeries() {
        if (!canPaginateSeries) return
        isLoadingData = true
        viewModelScope.launch {
            seriesRepository.getTopRatedSeries(currentSeriesPage).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _series.update { it + newSeries }
                }
                isLoadingData = false
            }
        }
    }

    private fun loadFavoriteSeries() {
        if (!canPaginateSeries) return
        isLoadingData = true
        viewModelScope.launch {
            seriesRepository.getFavoriteSeries(currentSeriesPage).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _series.update { it + newSeries }
                }
                isLoadingData = false
            }
        }
    }

    fun loadNextPage() {
        if (selectedCategory.value == "Películas" && canPaginateMovies) {
            currentMoviePage++
            when (sortBy.value) {
                SortType.ALPHABETIC -> loadMovies()
                SortType.RATING -> loadTopRatedMovies()
                SortType.FAVORITE -> loadFavoriteMovies()
            }
        } else if (canPaginateSeries) {
            currentSeriesPage++
            when (sortBy.value) {
                SortType.ALPHABETIC -> loadSeries()
                SortType.RATING -> loadTopRatedSeries()
                SortType.FAVORITE -> loadFavoriteSeries()
            }
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

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return

        _sortBy.value = type

        // Reiniciar el estado
        _movies.value = emptyList()
        _series.value = emptyList()
        currentMoviePage = 1
        currentSeriesPage = 1
        canPaginateMovies = true
        canPaginateSeries = true

        // Cargar nuevos datos según el tipo de ordenación
        when (type) {
            SortType.ALPHABETIC -> {
                loadMovies()
                loadSeries()
            }
            SortType.RATING -> {
                loadTopRatedMovies()
                loadTopRatedSeries()
            }
            SortType.FAVORITE -> {
                loadFavoriteMovies()
                loadFavoriteSeries()
            }
        }
    }
}