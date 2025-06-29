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
import com.example.peliculasserieskotlin.features.shared.repository.UserRepository
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.peliculasserieskotlin.features.home.HomeUiState
import android.util.Log
import com.example.peliculasserieskotlin.core.model.GenreItem

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    /*---------------------------  UI STATE  ---------------------------*/

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

    private val _selectedGenres = MutableStateFlow<List<GenreItem>>(emptyList())
    val selectedGenres: StateFlow<List<GenreItem>> = _selectedGenres.asStateFlow()

    val pagedMediaItems: StateFlow<Flow<PagingData<MediaItem>>> = combine(
        _selectedCategory,
        _sortBy,
        _searchQuery,
        userRepository.currentUser,
        _selectedGenres
    ) { category, sort, query, currentUser, selectedGenres ->
        val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
        val isGuest = currentUser == null
        val genreIds = selectedGenres.map { it.id }.filter { it != -1 }
        
        if (sort == SortType.FAVORITE && !isGuest) {
            flowOf(PagingData.empty())
        } else if (sort == SortType.FAVORITE && isGuest) {
            _sortBy.value = SortType.RATING
            try {
                mediaRepository.getPagedMedia(mediaType, SortType.RATING, query, genreIds)
                    .cachedIn(viewModelScope)
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error al cargar los datos")
                flowOf(PagingData.empty())
            }
        } else {
            try {
                mediaRepository.getPagedMedia(mediaType, sort, query, genreIds)
                    .cachedIn(viewModelScope)
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error al cargar los datos")
                flowOf(PagingData.empty())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = flowOf(PagingData.empty())
    )

    val favoriteMediaItems: StateFlow<List<MediaItem>> = combine(
        _selectedCategory,
        _sortBy,
        userRepository.currentUser
    ) { category, sort, currentUser ->
        val isGuest = currentUser == null
        if (sort == SortType.FAVORITE && !isGuest) {
            val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
            try {
                favoriteRepository.getFavoriteMedia(mediaType)
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error al cargar favoritos")
                flowOf(emptyList())
            }
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cachedMediaItems: StateFlow<List<MediaItem>> = combine(
        _selectedCategory,
        _selectedGenres
    ) { category, selectedGenres ->
        val mediaType = if (category == "Películas") MediaType.MOVIE else MediaType.SERIES
        Pair(mediaType, selectedGenres)
    }
        .flatMapLatest { (mediaType, selectedGenres) ->
            mediaRepository.getMediaFromLocalDb(mediaType).map { mediaList ->
                if (selectedGenres.isEmpty()) {
                    mediaList
                } else {
                    // Filtrar por coincidencia de al menos un género
                    mediaList.filter { item ->
                        val itemGenreIds = item.genres?.map { it.id } ?: emptyList()
                        selectedGenres.any { it.id in itemGenreIds }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var searchJob: Job? = null

    init {
        // Observar cambios en la conectividad
        viewModelScope.launch {
            var wasOffline = false
            networkUtils.getNetworkStatusFlow().collect { isConnected ->
                Log.d("HomeViewModel", "Network status changed: isConnected=$isConnected")
                _uiState.update { it.copy(isOffline = !isConnected) }
                if (isConnected && wasOffline) {
                    reloadAllData()
                }
                wasOffline = !isConnected
            }
        }
        // Timer de respaldo para asegurar actualización del estado de red
        viewModelScope.launch {
            while (true) {
                delay(5000)
                val isConnected = networkUtils.isNetworkAvailable()
                if (uiState.value.isOffline == isConnected) {
                    Log.d("HomeViewModel", "Timer backup: Corrigiendo estado de red. isConnected=$isConnected")
                    _uiState.update { it.copy(isOffline = !isConnected) }
                }
            }
        }
    }

    private fun reloadAllData() {
        // Llama a los métodos necesarios para recargar datos desde la API
        loadMediaItems()
        // Si tienes otros datos que refrescar, agrégalos aquí
    }

    fun toggleFavorite(item: MediaItem, isFav: Boolean) = viewModelScope.launch {
        // No permitir favoritos si es invitado
        if (!userRepository.isUserLoggedIn()) return@launch
        
        try {
            if (isFav) favoriteRepository.addFavorite(item)
            else favoriteRepository.removeFavorite(item.id, item.type)
        } catch (e: Exception) {
            updateError(e.localizedMessage ?: "Error al actualizar favoritos")
        }
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        if (!userRepository.isUserLoggedIn()) flowOf(false) else favoriteRepository.isFavorite(id, type)

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
        // No permitir cambiar a favoritos si es invitado
        if (type == SortType.FAVORITE && !userRepository.isUserLoggedIn()) return
        _sortBy.value = type
    }

    private fun updateLoading(value: Boolean) =
        _uiState.update { it.copy(isLoading = value) }

    private fun updateError(errorMsg: String?) =
        _uiState.update { it.copy(error = errorMsg, isLoading = false, isSearching = false) }

    /*----------- NAVEGACIÓN DETALLE -----------*/
    private val _navigateToDetail = MutableSharedFlow<Pair<Int, MediaType>>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    private val _showOfflineDetailWarning = MutableSharedFlow<Unit>()
    val showOfflineDetailWarning = _showOfflineDetailWarning.asSharedFlow()

    fun onMediaItemSelected(mediaItem: MediaItem) {
        viewModelScope.launch {
            if (!networkUtils.isNetworkAvailable()) {
                // Verificar si los detalles están en caché
                val hasDetails = mediaRepository.hasDetailsCached(mediaItem.id, mediaItem.type)
                if (hasDetails) {
                    _navigateToDetail.emit(mediaItem.id to mediaItem.type)
                } else {
                    // Mostrar advertencia de que los detalles no están disponibles offline
                    _showOfflineDetailWarning.emit(Unit)
                }
            } else {
                _navigateToDetail.emit(mediaItem.id to mediaItem.type)
            }
        }
    }

    fun loadMediaItems() {
        viewModelScope.launch {
            try {
                updateLoading(true)
                // La carga se maneja automáticamente a través de los Flows
                updateLoading(false)
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun clearError() {
        updateError(null)
    }

    fun toggleGenreSelection(genre: GenreItem) {
        if (genre.id == -1) {
            // Si es el chip 'Todos', limpiar selección
            _selectedGenres.value = emptyList()
        } else {
            _selectedGenres.value = if (_selectedGenres.value.contains(genre)) {
                _selectedGenres.value - genre
            } else {
                _selectedGenres.value + genre
            }
        }
    }
}
