package com.example.peliculasserieskotlin.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.peliculasserieskotlin.core.model.Serie
import com.example.peliculasserieskotlin.features.auth.AuthViewModel
import com.example.peliculasserieskotlin.features.shared.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val selectedGenres by viewModel.selectedGenres.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest = currentUser == null
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val pagedItems = viewModel.pagedSeries.collectAsState().value.collectAsLazyPagingItems()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()
    val cachedSeries by viewModel.cachedSeries.collectAsState()
    
    val listState = rememberLazyGridState()
    
    // Mostrar mensaje de sin conexión
    LaunchedEffect(uiState.isOffline) {
        if (uiState.isOffline) {
            snackbarHostState.showSnackbar(
                message = "Sin conexión a internet. Mostrando contenido en caché.",
                duration = SnackbarDuration.Long
            )
        }
    }
    
    // Mostrar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!inlineSearchActive && !showSearchBarForced) {
                SearchFab(
                    onClick = { viewModel.showInlineSearch() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con categoría
            HomeHeader(
                selectedCategory = "Series",
                onCategorySelected = { /* No hacer nada, solo series */ },
                searchText = viewModel.searchText,
                onSearchQueryChanged = viewModel::onSearchTextChanged,
                sortBy = sortBy,
                onSortTypeSelected = viewModel::onSortChanged,
                inlineSearchActive = inlineSearchActive
            )
            
            if (inlineSearchActive || showSearchBarForced) {
                InlineSearchTextField(
                    searchText = viewModel.searchText,
                    onSearchQueryChanged = viewModel::onSearchTextChanged,
                    selectedCategory = "Series",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Eliminar GenreFilterRow y SortButton duplicado
            // ...
            // El resto del contenido principal sigue igual
        }
    }
}

@Composable
private fun FavoriteContent(
    items: List<Serie>,
    listState: LazyGridState,
    viewModel: HomeViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    if (items.isEmpty()) {
        EmptyState(message = "No tienes series favoritas")
    } else {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                val isFavorite by viewModel.isFavorite(item.id)
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleFavorite(serie, newFavoriteState) } } else null,
                    onItemClick = { onNavigateToDetail(it.id) },
                    showFavoriteIcon = !isGuest
                )
            }
        }
    }
}

@Composable
private fun CachedContent(
    items: List<Serie>,
    listState: LazyGridState,
    viewModel: HomeViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    if (items.isEmpty()) {
        EmptyState(message = "No hay contenido en caché disponible")
    } else {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                val isFavorite by viewModel.isFavorite(item.id)
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleFavorite(serie, newFavoriteState) } } else null,
                    onItemClick = { onNavigateToDetail(it.id) },
                    showFavoriteIcon = !isGuest
                )
            }
        }
    }
}

@Composable
private fun PaginatedContent(
    pagedItems: androidx.paging.compose.LazyPagingItems<Serie>,
    listState: LazyGridState,
    viewModel: HomeViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = pagedItems.itemCount,
            key = { index -> pagedItems[index]?.id ?: -1 }
        ) { index ->
            val item = pagedItems[index]
            if (item != null) {
                val isFavorite by viewModel.isFavorite(item.id)
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleFavorite(serie, newFavoriteState) } } else null,
                    onItemClick = { onNavigateToDetail(item.id) },
                    showFavoriteIcon = !isGuest
                )
            }
        }
        
        // Handle loading states
        pagedItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadingState()
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadingState()
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val error = loadState.refresh as LoadState.Error
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorCard(
                            message = error.error.localizedMessage ?: "Error desconocido"
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    val error = loadState.append as LoadState.Error
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorCard(
                            message = error.error.localizedMessage ?: "Error al cargar más elementos"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}