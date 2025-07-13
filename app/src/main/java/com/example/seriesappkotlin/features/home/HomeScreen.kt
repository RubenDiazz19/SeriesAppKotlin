package com.example.seriesappkotlin.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    val currentUser by authViewModel.currentUser.collectAsState()
    val isGuest = currentUser == null

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
            FilterBottomBar()
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
                        viewModel = viewModel,
                        isGuest = isGuest,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
                else -> {
                    ModernPaginatedGrid(
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
                    imageVector = Icons.Default.Info, // Icono cambiado
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

@Composable
private fun FilterBottomBar() {
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
                onClick = { /* TODO: Implementar filtros */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700) // Color dorado como en la imagen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.List, // Icono cambiado
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
    viewModel: HomeViewModel,
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
                val isWatched by viewModel.isWatched(item.id)
                    .collectAsState(initial = false)

                ModernMediaCard(
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
private fun ModernPaginatedGrid(
    pagedItems: androidx.paging.compose.LazyPagingItems<Serie>,
    listState: LazyGridState,
    viewModel: HomeViewModel,
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
                val isWatched by viewModel.isWatched(item.id)
                    .collectAsState(initial = false)

                ModernMediaCard(
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
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}