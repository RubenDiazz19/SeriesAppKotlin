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

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    private var currentMoviePage = 1
    private var currentSeriesPage = 1
    private var canPaginateMovies = true
    private var canPaginateSeries = true

    var searchText by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    enum class SortType { ALPHABETIC, RATING, FAVORITE }

    private val _sortBy = MutableStateFlow(SortType.RATING)
    val sortBy: StateFlow<SortType> = _sortBy.asStateFlow()

    init {
        loadInitialData()
        setupBackHandler()
    }

    private fun loadInitialData() {
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

    private fun loadMovies() {
        if (!canPaginateMovies) return
        updateLoading(true)
        viewModelScope.launch {
            movieRepository.getMovies(currentMoviePage, genre = null).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _uiState.update { it.copy(movies = it.movies + newMovies) }
                }
                updateLoading(false)
            }
        }
    }

    private fun loadTopRatedMovies() {
        if (!canPaginateMovies) return
        updateLoading(true)
        viewModelScope.launch {
            movieRepository.getTopRatedMovies(currentMoviePage).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _uiState.update { it.copy(movies = it.movies + newMovies) }
                }
                updateLoading(false)
            }
        }
    }

    private fun loadFavoriteMovies() {
        if (!canPaginateMovies) return
        updateLoading(true)
        viewModelScope.launch {
            movieRepository.getFavoriteMovies(currentMoviePage).collect { newMovies ->
                if (newMovies.isEmpty()) {
                    canPaginateMovies = false
                } else {
                    _uiState.update { it.copy(movies = it.movies + newMovies) }
                }
                updateLoading(false)
            }
        }
    }

    private fun loadSeries() {
        if (!canPaginateSeries) return
        updateLoading(true)
        viewModelScope.launch {
            seriesRepository.getSeries(currentSeriesPage, genre = null).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _uiState.update { it.copy(series = it.series + newSeries) }
                }
                updateLoading(false)
            }
        }
    }

    private fun loadTopRatedSeries() {
        if (!canPaginateSeries) return
        updateLoading(true)
        viewModelScope.launch {
            seriesRepository.getTopRatedSeries(currentSeriesPage).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _uiState.update { it.copy(series = it.series + newSeries) }
                }
                updateLoading(false)
            }
        }
    }

    private fun loadFavoriteSeries() {
        if (!canPaginateSeries) return
        updateLoading(true)
        viewModelScope.launch {
            seriesRepository.getFavoriteSeries(currentSeriesPage).collect { newSeries ->
                if (newSeries.isEmpty()) {
                    canPaginateSeries = false
                } else {
                    _uiState.update { it.copy(series = it.series + newSeries) }
                }
                updateLoading(false)
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

        val localMovies = uiState.value.movies.filter {
            it.title.contains(query, ignoreCase = true)
        }
        val localSeries = uiState.value.series.filter {
            it.name.contains(query, ignoreCase = true)
        }

        _uiState.update { it.copy(
            movies = localMovies,
            series = localSeries,
            isSearching = true,
            error = null
        )}

        searchJob?.cancel()

        if (localMovies.isNotEmpty() || localSeries.isNotEmpty()) return

        searchJob = viewModelScope.launch {
            delay(500)
            searchRemotely(query)
        }
    }

    private fun searchRemotely(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }
            try {
                val remoteMovies = movieRepository.searchMovies(query)
                val remoteSeries = seriesRepository.searchSeries(query)
                _uiState.update { it.copy(
                    movies = remoteMovies,
                    series = remoteSeries,
                    isSearching = false,
                    error = null
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, error = e.message) }
            }
        }
    }

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return

        _sortBy.value = type

        // Reiniciar el estado
        _uiState.value = HomeUiState()
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

    private fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}