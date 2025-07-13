package com.example.seriesappkotlin.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.util.NetworkUtils
import com.example.seriesappkotlin.features.shared.repository.SerieRepository
import com.example.seriesappkotlin.features.shared.repository.UserRepository
import com.example.seriesappkotlin.features.shared.repository.WatchedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val serieRepository: SerieRepository,
    private val watchedRepository: WatchedRepository,
    private val userRepository: UserRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    enum class SortType { POPULAR, RATING, WATCHED }

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

    // Agregar estas líneas después de _selectedGenres
    private val _showGenreFilter = MutableStateFlow(false)
    val showGenreFilter: StateFlow<Boolean> = _showGenreFilter.asStateFlow()

    val pagedSeries: StateFlow<Flow<PagingData<Serie>>> = combine(
        _sortBy,
        _searchQuery,
        userRepository.currentUser,
        _selectedGenres
    ) { sort, query, currentUser, selectedGenres ->
        val isGuest = currentUser == null
        val genreIds = selectedGenres.map { it.id }.filter { it != -1 }

        if (sort == SortType.WATCHED && !isGuest) {
            flowOf(PagingData.empty())
        } else {
            try {
                serieRepository.getPagedSeries(sort, query, genreIds)
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

    val watchedSeries: StateFlow<List<Serie>> = combine(
        _sortBy,
        userRepository.currentUser
    ) { sort, currentUser ->
        val isGuest = currentUser == null
        if (sort == SortType.WATCHED && !isGuest) {
            try {
                watchedRepository.getWatchedSeries()
            } catch (e: Exception) {
                updateError(e.localizedMessage ?: "Error al cargar las series vistas")
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

    val cachedSeries: StateFlow<List<Serie>> = _selectedGenres
        .flatMapLatest { selectedGenres ->
            serieRepository.getSeriesFromLocalDb().map { serieList ->
                if (selectedGenres.isEmpty()) {
                    serieList
                } else {
                    serieList.filter { item ->
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
        viewModelScope.launch {
            var wasOffline = false
            networkUtils.getNetworkStatusFlow().collect { isConnected ->
                _uiState.update { it.copy(isOffline = !isConnected) }
                if (isConnected && wasOffline) {
                    reloadAllData()
                }
                wasOffline = !isConnected
            }
        }
        viewModelScope.launch {
            while (true) {
                delay(5000)
                val isConnected = networkUtils.isNetworkAvailable()
                if (uiState.value.isOffline == isConnected) {
                    _uiState.update { it.copy(isOffline = !isConnected) }
                }
            }
        }
    }

    private fun reloadAllData() {
        loadSeries()
    }

    fun toggleWatched(item: Serie, isWatched: Boolean) = viewModelScope.launch {
        if (!userRepository.isUserLoggedIn()) return@launch

        try {
            if (isWatched) watchedRepository.addWatched(item)
            else watchedRepository.removeWatched(item.id)
        } catch (e: Exception) {
            updateError(e.localizedMessage ?: "Error al actualizar las series vistas")
        }
    }

    fun isWatched(id: Int): Flow<Boolean> =
        if (!userRepository.isUserLoggedIn()) flowOf(false) else watchedRepository.isWatched(id)

    fun showInlineSearch() {
        _inlineSearchActive.value = true
        _showSearchBarForced.value = false
    }

    // Agregar después de _showSearchBarForced
    private val _hasActiveSearch = MutableStateFlow(false)
    val hasActiveSearch: StateFlow<Boolean> = _hasActiveSearch.asStateFlow()

    fun hideInlineSearch() {
        _inlineSearchActive.value = false
        // No limpiar searchText ni _searchQuery aquí para mantener la búsqueda aplicada
    }

    // Nueva función para limpiar completamente la búsqueda
    fun clearSearch() {
        _inlineSearchActive.value = false
        searchText = ""
        _searchQuery.value = null
        _hasActiveSearch.value = false
    }

    // Modificar onSearchTextChanged para actualizar hasActiveSearch
    fun onSearchTextChanged(text: String) {
        searchText = text
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _searchQuery.value = text
            _hasActiveSearch.value = text.isNotEmpty()
        }
    }

    fun forceShowSearchBar() {
        _showSearchBarForced.value = true
    }

    fun onSortChanged(sortType: SortType) {
        _sortBy.value = sortType
    }

    fun onGenreSelected(genre: GenreItem) {
        val currentSelection = _selectedGenres.value.toMutableList()
        if (currentSelection.contains(genre)) {
            currentSelection.remove(genre)
        } else {
            currentSelection.add(genre)
        }
        _selectedGenres.value = currentSelection
    }

    // Estas funciones ya están definidas correctamente
    fun showGenreFilter() {
        _showGenreFilter.value = true
    }

    fun hideGenreFilter() {
        _showGenreFilter.value = false
    }

    fun clearAllGenres() {
        _selectedGenres.value = emptyList()
    }

    private fun loadSeries() {
        // This is a placeholder. The actual data loading is handled by the flows.
    }

    private fun updateError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
