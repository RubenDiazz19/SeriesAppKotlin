package com.example.peliculasserieskotlin.presentation.home

import android.util.Log
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
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel.SortType

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val movies            by viewModel.movies.collectAsState()
    val series            by viewModel.series.collectAsState()
    val filteredMovies    by viewModel.filteredMovies
    val filteredSeries    by viewModel.filteredSeries
    val searchText        by remember { derivedStateOf { viewModel.searchText } }
    val isSearching       by remember { derivedStateOf { viewModel.isSearchingRemotely } }
    val sortBy            by viewModel.sortBy.collectAsState()
    val inlineSearch      by viewModel.inlineSearchActive.collectAsState()

    val listState = rememberLazyGridState()

    // Detectamos si estamos al tope para mostrar el header (dropdown + buscador + botones)
    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    // Construimos la lista final ya filtrada + ordenada
    val itemsToDisplay: List<Any> by remember(
        selectedCategory, searchText,
        movies, series,
        filteredMovies, filteredSeries,
        sortBy
    ) {
        derivedStateOf {
            // Base: pelis o series + filtro de búsqueda
            val base = when {
                selectedCategory == "Películas" && searchText.isNotBlank() ->
                    filteredMovies
                selectedCategory == "Películas" ->
                    movies
                searchText.isNotBlank() ->
                    filteredSeries
                else ->
                    series
            }
            // Aplicar orden según sortBy
            when (sortBy) {
                SortType.ALPHABETIC -> base.sortedWith(compareBy {
                    when (it) {
                        is Movie  -> it.title.lowercase()
                        is Series -> it.name.lowercase()
                        else      -> ""
                    }
                })
                SortType.RATING -> base.sortedWith(compareByDescending {
                    when (it) {
                        is Movie  -> it.voteAverage
                        is Series -> it.voteAverage
                        else      -> 0.0
                    }
                })
                SortType.FAVORITE -> base // favoritos real to-do
            }
        }
    }

    // Detección de scroll para paginar
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
            // HEADER (solo si estamos arriba)
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                Column {
                    // Dropdown de categorías
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
                    // Buscador principal
                    if (!inlineSearch) {
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
                        // Menú
                        IconButton(
                            onClick = { viewModel.setSortType(SortType.ALPHABETIC) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Orden alfabético",
                                tint = if (sortBy == SortType.ALPHABETIC)
                                    Color.Blue
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        // ⭐ Valoración
                        IconButton(
                            onClick = { viewModel.setSortType(SortType.RATING) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Orden por valoración",
                                tint = if (sortBy == SortType.RATING)
                                    Color(0xFFF4C10F)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        // ❤️ Favoritos
                        IconButton(
                            onClick = { viewModel.setSortType(SortType.FAVORITE) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Orden por favoritos",
                                tint = if (sortBy == SortType.FAVORITE)
                                    Color.Red
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // CUERPO: grid de items o loading
            if (isSearching) {
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
                        .padding(vertical = 4.dp)  // mismo padding arriba y abajo
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

        // BUSCADOR INLINE flotante
        AnimatedVisibility(
            visible = inlineSearch,
            enter   = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit    = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier = Modifier
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

        // FAB para abrir el buscador inline
        AnimatedVisibility(
            visible = !showHeader && !inlineSearch,
            enter   = fadeIn(),
            exit    = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick       = { viewModel.showInlineSearch() },
                shape         = CircleShape,
                containerColor= MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                elevation     = FloatingActionButtonDefaults.elevation(6.dp),
                modifier      = Modifier
                    .size(50.dp)
                    .border(1.dp, Color.Black, CircleShape)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Mostrar buscador",
                    modifier = Modifier.size(24.dp),
                    tint     = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
