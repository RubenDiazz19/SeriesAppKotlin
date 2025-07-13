package com.example.seriesappkotlin.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.auth.AuthViewModel
import com.example.seriesappkotlin.features.shared.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest = currentUser == null
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val pagedItems = viewModel.pagedSeries.collectAsState().value.collectAsLazyPagingItems()
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
            // Header simplificado - eliminar parámetros de sorting
            SimpleHeader(
                searchText = viewModel.searchText,
                onSearchQueryChanged = viewModel::onSearchTextChanged,
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
            
            // Simplificar la lógica - solo mostrar contenido normal o en caché
            when {
                uiState.isOffline -> {
                    CachedContent(
                        items = cachedSeries,
                        listState = listState,
                        viewModel = viewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                else -> {
                    PaginatedContent(
                        pagedItems = pagedItems,
                        listState = listState,
                        viewModel = viewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

// Agregar un header simplificado
@Composable
private fun SimpleHeader(
    searchText: String,
    onSearchQueryChanged: (String) -> Unit,
    inlineSearchActive: Boolean
) {
    // Implementación simple del header sin sorting
    Text(
        text = "Series",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun WatchedContent(
    items: List<Serie>,
    listState: LazyGridState,
    viewModel: HomeViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    if (items.isEmpty()) {
        EmptyState(message = "No tienes series vistas") // Cambiar mensaje
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
                val isWatched by viewModel.isWatched(item.id) // Cambiar de isFavorite a isWatched
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isWatched, // Usar isWatched
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleWatched(serie, newFavoriteState) } } else null, // Cambiar de toggleFavorite a toggleWatched
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
                val isWatched by viewModel.isWatched(item.id)
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isWatched,
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleWatched(serie, newFavoriteState) } } else null,
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
                val isWatched by viewModel.isWatched(item.id)
                    .collectAsState(initial = false)

                MediaCard(
                    serie = item,
                    isFavorite = isWatched,
                    onFavoriteClick = if (!isGuest) { { serie, newFavoriteState -> viewModel.toggleWatched(serie, newFavoriteState) } } else null,
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

// Eliminar completamente la función WatchedContent ya que no se usa