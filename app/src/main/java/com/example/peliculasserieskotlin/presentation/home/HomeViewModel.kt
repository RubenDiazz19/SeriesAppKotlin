package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.peliculasserieskotlin.data.repository.FavoriteRepository
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

    // Renombrar para evitar conflicto
    enum class SortType { ALPHABETIC, RATING, FAVORITE }

    private val _selectedCategory = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortBy = MutableStateFlow(SortType.RATING)
    val sortBy: StateFlow<SortType> = _sortBy.asStateFlow()

    /*-------------  buscador  -------------*/

    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    var searchText by mutableStateOf("")
        private set

    /*-------------  Paging Data  -------------*/

    private val _searchQuery = MutableStateFlow<String?>(null)
    
    // Flow para datos paginados de API
    // Asegurarnos de que todas las referencias usen el tipo correcto
    val pagedMediaItems: StateFlow<Flow<PagingData<MediaItem>>> = combine(
        _selectedCategory,
        _sortBy,
        _searchQuery
    ) { category, sort, query ->
        val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
        
        if (sort == SortType.FAVORITE) {
            // Para favoritos, usar el flow normal
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

    // Flow para favoritos (sin paginación)
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

    /*###################################################################*/
    /* -----------------------  FAVORITOS  ----------------------------- */
    /*###################################################################*/

    fun toggleFavorite(item: MediaItem, isFav: Boolean) = viewModelScope.launch {
        if (isFav) favoriteRepository.addFavorite(item)
        else favoriteRepository.removeFavorite(item.id, item.type)
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        favoriteRepository.isFavorite(id, type)

    /*###################################################################*/
    /* -----------------------  BUSCADOR  ------------------------------ */
    /*###################################################################*/

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
            return
        }

        _uiState.update { it.copy(isSearching = true, error = null) }

        searchJob = viewModelScope.launch {
            delay(800)
            _searchQuery.value = query
            _uiState.update { it.copy(isSearching = false) }
        }
    }

    /*###################################################################*/
    /* -----------------------  HELPERS  ------------------------------- */
    /*###################################################################*/

    fun updateCategory(category: String) {
        _selectedCategory.value = category
        // El cambio se propaga automáticamente a través de los flows combinados
    }

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return
        _sortBy.value = type
        // El cambio se propaga automáticamente a través de los flows combinados
    }

    private fun updateLoading(value: Boolean) =
        _uiState.update { it.copy(isLoading = value) }
}
