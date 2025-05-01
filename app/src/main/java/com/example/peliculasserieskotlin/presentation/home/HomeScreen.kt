package com.example.peliculasserieskotlin.presentation.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.domain.model.Series

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val selectedCategory      by viewModel.selectedCategory.collectAsState()
    val movies               by viewModel.movies.collectAsState()
    val series               by viewModel.series.collectAsState()
    val filteredMovies       by viewModel.filteredMovies
    val filteredSeries       by viewModel.filteredSeries
    val searchText           by remember { derivedStateOf { viewModel.searchText } }
    val isSearching          by remember { derivedStateOf { viewModel.isSearchingRemotely } }
    val isLoading            by remember { derivedStateOf { viewModel.isLoadingData } }
    val sortBy               by viewModel.sortBy.collectAsState()
    val inlineSearchActive   by viewModel.inlineSearchActive.collectAsState()

    val listState = rememberLazyGridState()


    // Si el buscador flotante está activo, al Back: ocultarlo y limpiar texto
    BackHandler(enabled = inlineSearchActive) {
        viewModel.hideInlineSearch()
        viewModel.onSearchQueryChanged("")
    }
    // Si no hay buscador flotante y hay texto en la cabecera, al Back: limpiar texto
    BackHandler(enabled = !inlineSearchActive && searchText.isNotBlank()) {
        viewModel.onSearchQueryChanged("")
    }

    // Detectar scroll-top para mostrar cabecera
    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    // Items finales (sin ordenar localmente; la VM recupera ya ordenados)
    val itemsToDisplay: List<Any> by remember(
        selectedCategory, searchText,
        movies, series,
        filteredMovies, filteredSeries
    ) {
        derivedStateOf {
            when {
                selectedCategory == "Películas" && searchText.isNotBlank() -> filteredMovies
                selectedCategory == "Películas" -> movies
                searchText.isNotBlank() -> filteredSeries
                else -> series
            }
        }
    }

    // Paginación
    val shouldLoadMore by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // CABECERA: dropdown + buscador + botones de orden
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 24.dp) // Espacio superior añadido aquí
                ) {
                    // Dropdown centrado
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryDropdown(
                            selectedCategory = selectedCategory,
                            onCategorySelected = viewModel::updateCategory
                        )
                    }
                    // Buscador de cabecera
                    if (!inlineSearchActive) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("Buscar...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor          = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    // Botones de orden
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        //★ Valoración
                        IconButton(
                            onClick = { viewModel.setSortType(HomeViewModel.SortType.RATING) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "★",
                                tint = if (sortBy == HomeViewModel.SortType.RATING)
                                    Color(0xFFF4C10F) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                        //Orden Alfabético
                        IconButton(
                            onClick = { viewModel.setSortType(HomeViewModel.SortType.ALPHABETIC) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "A–Z",
                                tint = if (sortBy == HomeViewModel.SortType.ALPHABETIC)
                                    Color.Blue else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                        //❤ Favoritos
                        IconButton(
                            onClick = { viewModel.setSortType(HomeViewModel.SortType.FAVORITE) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "❤️",
                                tint = if (sortBy == HomeViewModel.SortType.FAVORITE)
                                    Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // CUERPO
            if (isSearching || isLoading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    state   = listState,
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp)  // mismo padding arriba/abajo
                ) {
                    items(itemsToDisplay) { item ->
                        when (item) {
                            is Movie -> MovieSeriesItem(
                                title    = item.title,
                                imageUrl = item.posterUrl,
                                rating   = item.voteAverage
                            )
                            is Series -> MovieSeriesItem(
                                title    = item.name,
                                imageUrl = item.posterUrl,
                                rating   = item.voteAverage
                            )
                        }
                    }
                }
            }
        }

        // BUSCADOR INLINE
        AnimatedVisibility(
            visible = inlineSearchActive,
            enter   = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit    = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier= Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .padding(top = 48.dp)
        ) {
            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Buscar ${selectedCategory.lowercase()}...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor          = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // FAB para inline
        AnimatedVisibility(
            visible = !showHeader && !inlineSearchActive,
            enter   = fadeIn(),
            exit    = fadeOut(),
            modifier= Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick        = { viewModel.showInlineSearch() },
                shape          = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                elevation      = FloatingActionButtonDefaults.elevation(6.dp),
                modifier       = Modifier
                    .size(50.dp)
                    .border(1.dp, Color.Black, CircleShape)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "🔍",
                    modifier = Modifier.size(24.dp),
                    tint     = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
