package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.peliculasserieskotlin.data.repository.MediaRepository
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    private var currentPage = 1
    private var canPaginate = true

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
        val mediaType = if (selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
        loadMedia(sortBy.value, mediaType)
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
        // Recargar datos cuando cambia la categoría
        resetPagination()
        loadInitialData()
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

    private fun loadMedia(sortType: SortType, mediaType: MediaType) {
        if (!canPaginate) return
        updateLoading(true)
        
        viewModelScope.launch {
            when (sortType) {
                SortType.ALPHABETIC -> {
                    mediaRepository.getPopularMedia(currentPage, genre = null, mediaType).collect { newMedia ->
                        handleMediaResult(newMedia)
                    }
                }
                SortType.RATING -> {
                    mediaRepository.getTopRatedMedia(currentPage, mediaType).collect { newMedia ->
                        handleMediaResult(newMedia)
                    }
                }
                SortType.FAVORITE -> {
                    mediaRepository.getDiscoverMedia(currentPage, mediaType).collect { newMedia ->
                        handleMediaResult(newMedia)
                    }
                }
            }
        }
    }

    private fun handleMediaResult(newMedia: List<MediaItem>) {
        if (newMedia.isEmpty()) {
            canPaginate = false
        } else {
            _uiState.update { it.copy(mediaItems = it.mediaItems + newMedia) }
            currentPage++
        }
        updateLoading(false)
    }

    fun loadNextPage() {
        if (canPaginate) {
            val type = if (selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
            loadMedia(sortBy.value, type)
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchText = query
        
        searchJob?.cancel()
        
        if (query.isEmpty()) {
            resetPagination()
            loadInitialData()
            return
        }
        
        _uiState.update { it.copy(isSearching = true, error = null) }
        
        searchJob = viewModelScope.launch {
            // Aumentar el debounce para evitar demasiadas llamadas a la API
            delay(800)
            
            // Primero buscar en la base de datos local
            val type = if (selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
            try {
                val localResults = mediaRepository.searchMedia(query, 1, type)
                
                if (localResults.isNotEmpty()) {
                    _uiState.update { it.copy(
                        mediaItems = localResults,
                        isSearching = false,
                        error = null
                    )}
                } else {
                    // Si no hay resultados locales, buscar remotamente
                    searchRemotely(query)
                }
            } catch (e: Exception) {
                searchRemotely(query)
            }
        }
    }

    private fun searchRemotely(query: String) {
        viewModelScope.launch {
            try {
                val type = if (selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
                val remoteMedia = mediaRepository.searchMedia(query, currentPage, type)
                
                // Guardar los resultados en la base de datos local para futuras búsquedas
                if (remoteMedia.isNotEmpty()) {
                    try {
                        mediaRepository.insertMediaToLocalDb(remoteMedia)
                    } catch (e: Exception) {
                        // Ignorar errores al guardar en la base de datos
                    }
                }
                
                _uiState.update { it.copy(
                    mediaItems = remoteMedia,
                    isSearching = false,
                    error = null
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSearching = false, 
                    error = "Error al buscar: ${e.message}"
                )}
            }
        }
    }

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return

        _sortBy.value = type

        // Reiniciar el estado
        resetPagination()
        
        // Cargar nuevos datos según el tipo de ordenación
        val mediaType = if (selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
        loadMedia(type, mediaType)
    }
    
    private fun resetPagination() {
        _uiState.value = HomeUiState()
        currentPage = 1
        canPaginate = true
    }

    private fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}