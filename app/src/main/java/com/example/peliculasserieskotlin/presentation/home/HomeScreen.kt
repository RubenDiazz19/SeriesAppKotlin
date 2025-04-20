package com.example.peliculasserieskotlin.presentation.home

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val series by viewModel.series.collectAsState()
    val filteredMovies by viewModel.filteredMovies
    val filteredSeries by viewModel.filteredSeries
    val searchText = viewModel.searchText
    val isSearching = viewModel.isSearchingRemotely

    val listState = rememberLazyGridState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()

    val showHeader = remember {
        derivedStateOf {
            val isAtTop = listState.firstVisibleItemScrollOffset == 0 &&
                    listState.firstVisibleItemIndex == 0
            isAtTop
        }
    }.value


    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryDropdown(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.updateCategory(it) }
                        )
                    }

                    if (!inlineSearchActive) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = { Text("Buscar...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp)
                ) {
                    if (selectedCategory == "Películas") {
                        val movieItems = if (searchText.isNotBlank()) filteredMovies else movies
                        items(movieItems) { movie ->
                            MovieSeriesItem(
                                title = movie.title,
                                imageUrl = movie.posterUrl,
                                rating = movie.voteAverage
                            )
                        }
                    } else {
                        val seriesItems = if (searchText.isNotBlank()) filteredSeries else series
                        items(seriesItems) { serie ->
                            MovieSeriesItem(
                                title = serie.name,
                                imageUrl = serie.posterUrl,
                                rating = serie.voteAverage
                            )
                        }
                    }
                }
            }

            val shouldLoadMore by remember {
                derivedStateOf {
                    val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    val totalItems = listState.layoutInfo.totalItemsCount
                    lastVisible >= totalItems - 3
                }
            }

            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) viewModel.loadNextPage()
            }
        }

        // Buscador flotante minimalista
        AnimatedVisibility(
            visible = inlineSearchActive,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .padding(top = 48.dp) // Añadimos padding adicional para bajar el buscador
        ) {
            // Buscador minimalista con fondo semitransparente
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Buscar " + selectedCategory.lowercase() + "...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // FAB de lupa
        AnimatedVisibility(
            visible = !showHeader && !inlineSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    Log.d("FAB", "FAB pulsado - activando buscador inline")
                    viewModel.showInlineSearch()
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                modifier = Modifier
                    .size(50.dp)
                    .border(1.dp, Color.Black, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Mostrar buscador",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}