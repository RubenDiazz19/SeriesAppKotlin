package com.example.seriesappkotlin.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.SUPPORTED_GENRES
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.auth.AuthViewModel
import com.example.seriesappkotlin.features.favorites.FavoriteViewModel
import com.example.seriesappkotlin.features.shared.components.*
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest = currentUser == null
    val showGenreFilter by viewModel.showGenreFilter.collectAsState()
    val selectedGenres by viewModel.selectedGenres.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val hasActiveSearch by viewModel.hasActiveSearch.collectAsState()
    
    // Agregar estas líneas para manejar favoritos
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val pagedItems = viewModel.pagedSeries.collectAsState().value.collectAsLazyPagingItems()
    val cachedSeries by viewModel.cachedSeries.collectAsState()

    val listState = rememberLazyGridState()

    // Manejar navegación hacia atrás
    BackHandler {
        when {
            inlineSearchActive -> {
                // Primer back: ocultar barra pero mantener búsqueda
                viewModel.hideInlineSearch()
            }
            hasActiveSearch -> {
                // Segundo back: limpiar búsqueda completamente
                viewModel.clearSearch()
            }
            else -> {
                // Tercer back: salir de la app (comportamiento por defecto)
                // No hacer nada, dejará que el sistema maneje la salida
            }
        }
    }

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
        // Modificar la llamada a ModernTopBar en HomeScreen
        topBar = {
            ModernTopBar(
                currentUser = currentUser,
                isGuest = isGuest,
                searchText = viewModel.searchText,
                isSearchActive = inlineSearchActive,
                showFavoritesOnly = viewModel.showFavoritesOnly.collectAsState().value,
                showWatchedOnly = viewModel.showWatchedOnly.collectAsState().value,
                onSearchClick = viewModel::showInlineSearch,
                onSearchTextChanged = viewModel::onSearchTextChanged,
                onSearchClose = viewModel::hideInlineSearch,
                onToggleFavorites = viewModel::toggleFavoritesFilter,
                onToggleWatched = viewModel::toggleWatchedFilter // Cambiar de viewModel::uiState a viewModel::toggleWatchedFilter
            )
        },
        bottomBar = {
            Column {
                // Filtro de géneros minimalista encima del botón
                if (showGenreFilter) {
                    MinimalistGenreFilter(
                        selectedGenres = selectedGenres,
                        onGenreSelected = viewModel::onGenreSelected,
                        onClearAll = viewModel::clearAllGenres
                    )
                }
                
                FilterBottomBar(
                    onFilterClick = { 
                        if (showGenreFilter) {
                            viewModel.hideGenreFilter()
                        } else {
                            viewModel.showGenreFilter()
                        }
                    },
                    isFilterActive = showGenreFilter || selectedGenres.isNotEmpty(),
                    filterCount = selectedGenres.size
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            when {
                // Agregar condición para mostrar favoritos
                showFavoritesOnly && !isGuest -> {
                    ModernSeriesGrid(
                        items = favoriteSeries,
                        listState = listState,
                        favoriteViewModel = favoriteViewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                uiState.isOffline -> {
                    ModernSeriesGrid(
                        items = cachedSeries,
                        listState = listState,
                        favoriteViewModel = favoriteViewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                else -> {
                    ModernPaginatedGrid(
                        pagedItems = pagedItems,
                        listState = listState,
                        favoriteViewModel = favoriteViewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MinimalistGenreFilter(
    selectedGenres: List<GenreItem>,
    onGenreSelected: (GenreItem) -> Unit,
    onClearAll: () -> Unit
) {
    // Solo la lista de géneros, sin header ni padding extra
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SUPPORTED_GENRES) { genre ->
            CompactGenreChip(
                genre = genre,
                isSelected = selectedGenres.contains(genre),
                onSelected = { onGenreSelected(genre) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactGenreChip(
    genre: GenreItem,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onSelected() }
            .height(32.dp),
        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre.name,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopBar(
    currentUser: com.example.seriesappkotlin.core.database.entity.UserEntity? = null,
    isGuest: Boolean = true,
    searchText: String = "",
    isSearchActive: Boolean = false,
    showFavoritesOnly: Boolean = false,
    showWatchedOnly: Boolean = false,
    onSearchClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
    onSearchClose: () -> Unit = {},
    onToggleFavorites: () -> Unit = {},
    onToggleWatched: () -> Unit = {}
) {
    TopAppBar(
        title = {
            if (isSearchActive) {
                // Barra de búsqueda mejorada con mejor visibilidad
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    placeholder = {
                        Text(
                            text = "Buscar una serie...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // Aumentar altura para mejor visibilidad
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.8f), // Más visible
                        cursorColor = Color(0xFFFFD700),
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f), // Fondo semi-transparente
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp), // Bordes más redondeados
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                )
            } else {
                Text(
                    text = if (isGuest) {
                        "Listado de Series"
                    } else {
                        "¡Bienvenido, ${currentUser?.username ?: "Usuario"}!"
                    },
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (isSearchActive) {
                // Botón para cerrar la búsqueda con mejor visibilidad
                IconButton(
                    onClick = onSearchClose,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar búsqueda",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                // Botones de filtros (solo para usuarios logueados)
                if (!isGuest) {
                    // Botón de favoritos
                    IconButton(
                        onClick = onToggleFavorites
                    ) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Filtrar favoritos",
                            tint = if (showFavoritesOnly) Color(0xFFFFD700) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Botón de vistas
                    IconButton(
                        onClick = onToggleWatched
                    ) {
                        Icon(
                            imageVector = if (showWatchedOnly) Icons.Filled.Check else Icons.Outlined.Check,
                            contentDescription = "Filtrar vistas",
                            tint = if (showWatchedOnly) Color(0xFFFFD700) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Botón para abrir la búsqueda
                IconButton(
                    onClick = onSearchClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomBar(
    onFilterClick: () -> Unit,
    isFilterActive: Boolean = false,
    filterCount: Int = 0
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onFilterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFilterActive) Color(0xFFFFD700).copy(alpha = 0.9f) else Color(0xFFFFD700)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Filtros",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (filterCount > 0) "FILTROS($filterCount)" else "FILTROS",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernSeriesGrid(
    items: List<Serie>,
    listState: LazyGridState,
    favoriteViewModel: FavoriteViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    if (items.isEmpty()) {
        EmptyState(message = "No hay contenido disponible")
    } else {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(3), // 3 columnas como en la imagen
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                if (!isGuest) {
                    val isFavorite by favoriteViewModel.isFavorite(item.id).collectAsState(initial = false)
                    ModernMediaCard(
                        serie = item,
                        isFavorite = isFavorite,
                        onFavoriteClick = { serie, newFavoriteState -> 
                            favoriteViewModel.toggleFavorite(serie, newFavoriteState) 
                        },
                        onItemClick = { onNavigateToDetail(it.id) },
                        showFavoriteIcon = true
                    )
                } else {
                    ModernMediaCard(
                        serie = item,
                        isFavorite = false,
                        onFavoriteClick = null,
                        onItemClick = { onNavigateToDetail(it.id) },
                        showFavoriteIcon = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernPaginatedGrid(
    pagedItems: LazyPagingItems<Serie>,
    listState: LazyGridState,
    favoriteViewModel: FavoriteViewModel,
    isGuest: Boolean,
    onNavigateToDetail: (Int) -> Unit
) {
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(3), // 3 columnas como en la imagen
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(
            count = pagedItems.itemCount,
            key = { index -> pagedItems[index]?.id ?: -1 }
        ) { index ->
            val item = pagedItems[index]
            if (item != null) {
                if (!isGuest) {
                    val isFavorite by favoriteViewModel.isFavorite(item.id).collectAsState(initial = false)
                    ModernMediaCard(
                        serie = item,
                        isFavorite = isFavorite,
                        onFavoriteClick = { serie, newFavoriteState -> 
                            favoriteViewModel.toggleFavorite(serie, newFavoriteState) 
                        },
                        onItemClick = { onNavigateToDetail(item.id) },
                        showFavoriteIcon = true
                    )
                } else {
                    ModernMediaCard(
                        serie = item,
                        isFavorite = false,
                        onFavoriteClick = null,
                        onItemClick = { onNavigateToDetail(item.id) },
                        showFavoriteIcon = false
                    )
                }
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
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}