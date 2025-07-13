package com.example.seriesappkotlin.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
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
        topBar = {
            ModernTopBar()
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
                    isFilterActive = showGenreFilter || selectedGenres.isNotEmpty()
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header compacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedGenres.isEmpty()) "Selecciona géneros" else "Géneros (${selectedGenres.size})",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                if (selectedGenres.isNotEmpty()) {
                    TextButton(
                        onClick = onClearAll,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Limpiar",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Lista compacta de géneros
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
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
            .height(28.dp),
        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF333333),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = genre.name,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Series",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(
                onClick = { /* TODO: Implementar acción del ojo */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Ver opciones",
                    tint = Color.White
                )
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
    isFilterActive: Boolean = false
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
                        text = "FILTROS",
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