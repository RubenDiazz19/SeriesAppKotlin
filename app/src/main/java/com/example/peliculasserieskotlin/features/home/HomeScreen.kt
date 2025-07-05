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
import com.example.peliculasserieskotlin.features.shared.components.LoadingState
import com.example.peliculasserieskotlin.features.shared.components.EmptyState
import com.example.peliculasserieskotlin.features.shared.components.ErrorState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.SUPPORTED_GENRES

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
    val selectedGenres by viewModel.selectedGenres.collectAsState()

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
                    ErrorState(message = uiState.error ?: "Error desconocido")
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
                                Column {
                                    HomeHeader(
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = viewModel::updateCategory,
                                        searchText = searchText,
                                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                        sortBy = sortBy,
                                        onSortTypeSelected = viewModel::setSortType,
                                        inlineSearchActive = inlineSearchActive,
                                        showFavoriteSort = !isGuest,
                                        actionsSlot = {
                                            GenreSelector(
                                                selectedGenres = selectedGenres,
                                                onGenreClick = viewModel::toggleGenreSelection,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        // Selector de géneros cuando el header no es visible
                        AnimatedVisibility(
                            visible = !showHeader && searchText.isBlank(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                tonalElevation = 1.dp
                            ) {
                                GenreSelector(
                                    selectedGenres = selectedGenres,
                                    onGenreClick = viewModel::toggleGenreSelection
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
                                        viewModel = viewModel,
                                        isGuest = isGuest
                                    )
                                }
                                isGuest && sortBy == HomeViewModel.SortType.FAVORITE -> {
                                    // Si es invitado y está en modo favoritos, mostrar contenido normal
                                    MainContent(
                                        pagedItems = pagedItems,
                                        targetMediaType = targetMediaType,
                                        listState = listState,
                                        viewModel = viewModel,
                                        isGuest = isGuest
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
                                        viewModel = viewModel,
                                        isGuest = isGuest
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
    // Eliminado, usar el componente reutilizable
}

@Composable
private fun LoadingState() {
    // Eliminado, usar el componente reutilizable
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
    viewModel: HomeViewModel,
    isGuest: Boolean
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
                    onFavoriteClick = if (!isGuest) { { mediaItem, newFavoriteState -> viewModel.toggleFavorite(mediaItem, newFavoriteState) } } else null,
                    onItemClick = { viewModel.onMediaItemSelected(it) },
                    showFavoriteIcon = !isGuest
                )
            }
        }
    }
}

@Composable
private fun EmptyFavorites() {
    EmptyState(
        icon = "💙",
        title = "Sin favoritos",
        message = "Marca contenido como favorito para verlo aquí"
    )
}

@Composable
private fun MainContent(
    pagedItems: androidx.paging.compose.LazyPagingItems<com.example.peliculasserieskotlin.core.model.MediaItem>,
    targetMediaType: MediaType,
    listState: LazyGridState,
    viewModel: HomeViewModel,
    isGuest: Boolean
) {
    Log.d("DEBUG", "[MainContent] isGuest: $isGuest")
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
                Log.d("DEBUG", "[MediaCard] isGuest: $isGuest, item: ${item.title}")
                MediaCard(
                    mediaItem = item,
                    isFavorite = isFavorite,
                    onFavoriteClick = if (!isGuest) { { mediaItem, newFavoriteState -> viewModel.toggleFavorite(mediaItem, newFavoriteState) } } else null,
                    onItemClick = { viewModel.onMediaItemSelected(it) },
                    showFavoriteIcon = !isGuest
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

@Composable
fun GenreSelector(
    selectedGenres: List<GenreItem>,
    onGenreClick: (GenreItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        // Opción "Todos" al inicio
        item {
            GenreChip(
                text = "Todos",
                isSelected = selectedGenres.isEmpty(),
                onClick = { /* Si se pulsa 'Todos', limpiar selección */ onGenreClick(GenreItem(-1, "Todos")) }
            )
        }
        // Lista de géneros
        items(SUPPORTED_GENRES) { genre ->
            GenreChip(
                text = genre.name,
                isSelected = selectedGenres.any { it.id == genre.id },
                onClick = { onGenreClick(genre) }
            )
        }
    }
}

@Composable
private fun GenreChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(200),
        label = "chip_color"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "text_color"
    )
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .heightIn(min = 36.dp),
        color = animatedColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                ),
                color = animatedTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}