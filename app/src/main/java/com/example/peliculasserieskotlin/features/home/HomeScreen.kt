package com.example.peliculasserieskotlin.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.shared.components.MediaCard
import com.example.peliculasserieskotlin.features.shared.components.HomeHeader
import com.example.peliculasserieskotlin.features.shared.components.InlineSearchTextField
import com.example.peliculasserieskotlin.features.shared.components.SearchFab
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.peliculasserieskotlin.core.util.NetworkUtils
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.peliculasserieskotlin.features.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Int, MediaType) -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest = currentUser == null
    
    Log.d("DEBUG", "[HomeScreen] isGuest: $isGuest")
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val searchText = viewModel.searchText

    val listState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pagedItems = viewModel.pagedMediaItems.collectAsState().value.collectAsLazyPagingItems()
    val favoriteItems by viewModel.favoriteMediaItems.collectAsState()
    val cachedItems by viewModel.cachedMediaItems.collectAsState()

    // Simulación de conectividad (reemplaza esto por una comprobación real si tienes NetworkUtils disponible por DI)
    val isOnline = pagedItems.loadState.refresh !is LoadState.Error

    // Feedback visual al recuperar la conexión
    var wasOffline by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isOffline) {
        if (!uiState.isOffline && wasOffline) {
            snackbarHostState.showSnackbar("Conexión restaurada, actualizando contenido...")

            // Siempre refresca la lista al reconectar
            pagedItems.refresh()

            // Si el usuario está al final de la lista y hubo error al cargar más elementos, forzar recarga de la siguiente página
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            if (lastVisible >= pagedItems.itemCount - 1 && pagedItems.loadState.append is LoadState.Error) {
                pagedItems.retry()
            }
        }
        wasOffline = uiState.isOffline
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToDetail.collectLatest { (id, type) ->
            onNavigateToDetail(id, type)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showOfflineDetailWarning.collectLatest {
            snackbarHostState.showSnackbar(
                message = "Los detalles de este contenido no están disponibles sin conexión",
                duration = SnackbarDuration.Short
            )
        }
    }

    BackHandler(enabled = inlineSearchActive) {
        viewModel.hideInlineSearch()
        viewModel.onSearchQueryChanged("")
    }

    BackHandler(enabled = !inlineSearchActive && searchText.isNotBlank()) {
        viewModel.onSearchQueryChanged("")
    }

    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 50
        }
    }

    val targetMediaType = if (selectedCategory == "Películas") MediaType.MOVIE else MediaType.SERIES

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            when {
                uiState.error != null -> {
                    ErrorState(error = uiState.error ?: "Error desconocido")
                }
                uiState.isLoading -> {
                    LoadingState()
                }
                else -> {
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        // Header con animación suave
                        AnimatedVisibility(
                            visible = showHeader,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                tonalElevation = if (showHeader) 0.dp else 2.dp
                            ) {
                                Log.d("DEBUG", "[HomeHeader] isGuest: $isGuest")
                                HomeHeader(
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = viewModel::updateCategory,
                                    searchText = searchText,
                                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                    sortBy = sortBy,
                                    onSortTypeSelected = viewModel::setSortType,
                                    inlineSearchActive = inlineSearchActive,
                                    showFavoriteSort = !isGuest
                                )
                            }
                        }

                        // Content area con padding mejorado
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        ) {
                            when {
                                uiState.isSearching -> {
                                    SearchingState()
                                }
                                !isGuest && sortBy == HomeViewModel.SortType.FAVORITE -> {
                                    FavoriteContent(
                                        items = favoriteItems.filter { it.type == targetMediaType },
                                        listState = listState,
                                        viewModel = viewModel
                                    )
                                }
                                isGuest && sortBy == HomeViewModel.SortType.FAVORITE -> {
                                    // Si es invitado y está en modo favoritos, mostrar contenido normal
                                    MainContent(
                                        pagedItems = pagedItems,
                                        targetMediaType = targetMediaType,
                                        listState = listState,
                                        viewModel = viewModel
                                    )
                                }
                                !isOnline -> {
                                    // Mostrar la caché local si no hay internet
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
                                            items = cachedItems,
                                            key = { it.id }
                                        ) { item ->
                                            val isFavorite by viewModel.isFavorite(item.id, item.type)
                                                .collectAsState(initial = false)

                                            MediaCard(
                                                mediaItem = item,
                                                isFavorite = isFavorite,
                                                onFavoriteClick = if (!isGuest) { { mediaItem, newFavoriteState -> viewModel.toggleFavorite(mediaItem, newFavoriteState) } } else null,
                                                onItemClick = { viewModel.onMediaItemSelected(it) },
                                                showFavoriteIcon = !isGuest
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    MainContent(
                                        pagedItems = pagedItems,
                                        targetMediaType = targetMediaType,
                                        listState = listState,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Inline search overlay
            AnimatedVisibility(
                visible = inlineSearchActive,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(top = 60.dp)
            ) {
                InlineSearchTextField(
                    searchText = searchText,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    selectedCategory = selectedCategory
                )
            }

            // Search FAB con mejor posicionamiento
            AnimatedVisibility(
                visible = !showHeader && !inlineSearchActive,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                SearchFab(
                    onClick = { viewModel.showInlineSearch() }
                )
            }

            // Indicador de modo offline
            AnimatedVisibility(
                visible = uiState.isOffline,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Modo sin conexión",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(error: String) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando contenido...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchingState() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Buscando...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteContent(
    items: List<com.example.peliculasserieskotlin.core.model.MediaItem>,
    listState: LazyGridState,
    viewModel: HomeViewModel
) {
    if (items.isEmpty()) {
        EmptyFavorites()
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
                val isFavorite by viewModel.isFavorite(item.id, item.type)
                    .collectAsState(initial = false)

                MediaCard(
                    mediaItem = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = { mediaItem, newFavoriteState -> viewModel.toggleFavorite(mediaItem, newFavoriteState) },
                    onItemClick = { viewModel.onMediaItemSelected(it) },
                    showFavoriteIcon = true
                )
            }
        }
    }
}

@Composable
private fun EmptyFavorites() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💙",
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sin favoritos",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Marca contenido como favorito para verlo aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    pagedItems: androidx.paging.compose.LazyPagingItems<com.example.peliculasserieskotlin.core.model.MediaItem>,
    targetMediaType: MediaType,
    listState: LazyGridState,
    viewModel: HomeViewModel
) {
    Log.d("DEBUG", "[MainContent] isGuest: false")
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
        items(pagedItems.itemCount) { index ->
            val item = pagedItems[index]
            if (item != null && item.type == targetMediaType) {
                val isFavorite by viewModel.isFavorite(item.id, item.type)
                    .collectAsState(initial = false)
                Log.d("DEBUG", "[MediaCard] isGuest: false, item: ${item.title}")
                MediaCard(
                    mediaItem = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = { mediaItem, newFavoriteState -> viewModel.toggleFavorite(mediaItem, newFavoriteState) },
                    onItemClick = { viewModel.onMediaItemSelected(it) },
                    showFavoriteIcon = true
                )
            }
        }

        // Loading indicator para paginación
        when (pagedItems.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Sin conexión. Por favor, vuelve a conectar a internet para continuar cargando.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            else -> {}
        }
    }

    // Handle refresh loading state
    when (pagedItems.loadState.refresh) {
        is LoadState.Loading -> {
            LoadingState()
        }

        is LoadState.Error -> {
            ErrorState("Error al cargar datos")
        }

        else -> {}
    }
}