package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val mediaItems: List<MediaItem> = emptyList(),
        val isLoading:  Boolean         = false,
        val isSearching:Boolean         = false,
        val error:      String?         = null
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    enum class SortType { ALPHABETIC, RATING, FAVORITE }

    private val _selectedCategory   = MutableStateFlow("Películas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortBy   = MutableStateFlow(SortType.RATING)
    val sortBy: StateFlow<SortType> = _sortBy.asStateFlow()

    /*-------------  buscador (no cambia)  -------------*/

    private val _inlineSearchActive = MutableStateFlow(false)
    val inlineSearchActive: StateFlow<Boolean> = _inlineSearchActive.asStateFlow()

    private val _showSearchBarForced = MutableStateFlow(false)
    val showSearchBarForced: StateFlow<Boolean> = _showSearchBarForced.asStateFlow()

    var searchText by mutableStateOf("")
        private set

    /*-------------  paginación y jobs  -------------*/

    private var currentPage  = 1
    private var canPaginate  = true
    private var searchJob: Job? = null
    private var favoritesJob: Job? = null           // ⭐

    init { loadInitialData() }

    /*###################################################################*/
    /* -----------------------  FAVORITOS  ----------------------------- */
    /*###################################################################*/

    fun toggleFavorite(item: MediaItem, isFav: Boolean) = viewModelScope.launch {
        if (isFav) favoriteRepository.addFavorite(item)
        else       favoriteRepository.removeFavorite(item.id, item.type)
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        favoriteRepository.isFavorite(id, type)

    /*###################################################################*/
    /* ---------------------  CARGA DE CONTENIDO  ----------------------- */
    /*###################################################################*/

    private fun loadInitialData() {
        val mediaType = if (_selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
        loadMedia(_sortBy.value, mediaType, refresh = true)
    }

    fun loadNextPage() {
        if (canPaginate) {
            val mediaType = if (_selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
            loadMedia(_sortBy.value, mediaType)
        }
    }

    private fun loadMedia(sortType: SortType, mediaType: MediaType, refresh: Boolean = false) {
        /*--- si veníamos escuchando favoritos y cambiamos de filtro, cancelamos ---*/
        if (sortType != SortType.FAVORITE) {
            favoritesJob?.cancel()
            favoritesJob = null
        }

        if (!canPaginate && !refresh && sortType != SortType.FAVORITE) return
        updateLoading(true)

        viewModelScope.launch {
            when (sortType) {
                SortType.ALPHABETIC -> {
                    mediaRepository.getPopularMedia(currentPage, null, mediaType)
                        .collect { handleMediaResult(it, refresh) }
                }
                SortType.RATING -> {
                    mediaRepository.getTopRatedMedia(currentPage, mediaType)
                        .collect { handleMediaResult(it, refresh) }
                }
                SortType.FAVORITE -> {
                    if (favoritesJob == null) {
                        canPaginate = false        // no hay paginación en favoritos
                        favoritesJob = favoriteRepository.getFavoriteMedia(mediaType)
                            .onEach { list ->
                                _uiState.update { it.copy(mediaItems = list) }
                                updateLoading(false)
                            }
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }

    private fun handleMediaResult(items: List<MediaItem>, refresh: Boolean) {
        if (refresh) _uiState.value = _uiState.value.copy(mediaItems = emptyList())
        if (items.isEmpty()) {
            canPaginate = false
        } else {
            _uiState.update { it.copy(mediaItems = it.mediaItems + items) }
            currentPage++
        }
        updateLoading(false)
    }

    /*###################################################################*/
    /* -----------------------  BUSCADOR  ------------------------------ */
    /*###################################################################*/

    fun showInlineSearch()  { _inlineSearchActive.value = true;  _showSearchBarForced.value = false }
    fun hideInlineSearch()  { _inlineSearchActive.value = false; _showSearchBarForced.value = false }

    fun onSearchQueryChanged(query: String) {
        searchText = query
        _showSearchBarForced.value = query.isNotBlank()

        searchJob?.cancel()
        if (query.isBlank()) {
            resetPagination()
            loadInitialData()
            return
        }

        _uiState.update { it.copy(isSearching = true, error = null) }

        searchJob = viewModelScope.launch {
            delay(800)

            val type = if (_selectedCategory.value == "Películas") MediaType.MOVIE else MediaType.SERIES
            try {
                val local = mediaRepository.searchMedia(query, 1, type)
                if (local.isNotEmpty()) {
                    _uiState.update { it.copy(mediaItems = local, isSearching = false) }
                } else {
                    searchRemote(query, type)
                }
            } catch (_: Exception) {
                searchRemote(query, type)
            }
        }
    }

    private fun searchRemote(query: String, type: MediaType) = viewModelScope.launch {
        try {
            val remote = mediaRepository.searchMedia(query, page = 1, type = type)
            if (remote.isNotEmpty()) {
                try { mediaRepository.insertMediaToLocalDb(remote) } catch (_: Exception) {}
            }
            _uiState.update { it.copy(mediaItems = remote, isSearching = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isSearching = false, error = e.message) }
        }
    }

    /*###################################################################*/
    /* -----------------------  HELPERS  ------------------------------- */
    /*###################################################################*/

    fun updateCategory(category: String) {
        _selectedCategory.value = category
        resetPagination()
        loadInitialData()
    }

    fun setSortType(type: SortType) {
        if (_sortBy.value == type) return
        _sortBy.value = type
        resetPagination()
        loadInitialData()
    }

    private fun resetPagination() {
        _uiState.value = HomeUiState()
        currentPage = 1
        canPaginate = true
        favoritesJob?.cancel()
        favoritesJob = null
        hideInlineSearch()
    }

    private fun updateLoading(value: Boolean) =
        _uiState.update { it.copy(isLoading = value) }
}
