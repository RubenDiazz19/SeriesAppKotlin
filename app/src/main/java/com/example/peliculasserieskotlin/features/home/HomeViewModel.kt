package com.example.peliculasserieskotlin.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.peliculasserieskotlin.features.shared.repository.FavoriteRepository
import com.example.peliculasserieskotlin.features.shared.repository.MediaRepository
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    /*---------------------------  UI STATE  ---------------------------*/

    data class HomeUiState(
        val isLoading: Boolean = false,
        val isSearching: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    enum class SortType { ALPHABETIC, RATING, FAVORITE }

    private val _selectedCategory = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortBy = MutableStateFlow(SortType.RATING)
    val sortBy: StateFlow<SortType> = _sortBy.asStateFlow()

    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    var searchText by mutableStateOf("")
        private set

    private val _searchQuery = MutableStateFlow<String?>(null)

    val pagedMediaItems: StateFlow<Flow<PagingData<MediaItem>>> = combine(
        _selectedCategory,
        _sortBy,
        _searchQuery
    ) { category, sort, query ->
        val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
        if (sort == SortType.FAVORITE) {
            flowOf(PagingData.empty())
        } else {
            mediaRepository.getPagedMedia(mediaType, sort, query)
                .cachedIn(viewModelScope)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = flowOf(PagingData.empty())
    )

    val favoriteMediaItems: StateFlow<List<MediaItem>> = combine(
        _selectedCategory,
        _sortBy
    ) { category, sort ->
        if (sort == SortType.FAVORITE) {
            val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
            favoriteRepository.getFavoriteMedia(mediaType)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var searchJob: Job? = null

    fun toggleFavorite(item: MediaItem, isFav: Boolean) = viewModelScope.launch {
        if (isFav) favoriteRepository.addFavorite(item)
        else favoriteRepository.removeFavorite(item.id, item.type)
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        favoriteRepository.isFavorite(id, type)

    fun showInlineSearch() {
        _inlineSearchActive.value = true
        _showSearchBarForced.value = false
    }

    fun hideInlineSearch() {
        _inlineSearchActive.value = false
        _showSearchBarForced.value = false
    }

    fun onSearchQueryChanged(query: String) {
        searchText = query
        _showSearchBarForced.value = query.isNotBlank()

        searchJob?.cancel()

        if (query.isBlank()) {
            _searchQuery.value = null
            _uiState.update { it.copy(isSearching = false) }
            return
        }

        _uiState.update { it.copy(isSearching = true, error = null) }

        searchJob = viewModelScope.launch {
            delay(800)
            _searchQuery.value = query
            _uiState.update { it.copy(isSearching = false) }
        }
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return
        _sortBy.value = type
    }

    private fun updateLoading(value: Boolean) =
        _uiState.update { it.copy(isLoading = value) }

    private fun updateError(errorMsg: String?) =
        _uiState.update { it.copy(error = errorMsg, isLoading = false, isSearching = false) }

    /*----------- NAVEGACIÓN DETALLE -----------*/
    private val _navigateToDetail = MutableSharedFlow<Pair<Int, MediaType>>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    fun onMediaItemSelected(mediaItem: MediaItem) {
        viewModelScope.launch {
            _navigateToDetail.emit(mediaItem.id to mediaItem.type)
        }
    }

    fun loadMediaItems() {
        viewModelScope.launch {
            try {
                updateLoading(true)
                // Simulación de carga, reemplaza por tu lógica real
                // val result = mediaRepository.getPagedMedia(...)
                // if (result is Result.Error) updateError(result.exception.localizedMessage)
                // else ...
                updateLoading(false)
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

}
