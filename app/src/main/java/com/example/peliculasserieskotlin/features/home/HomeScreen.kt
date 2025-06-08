package com.example.peliculasserieskotlin.features.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.shared.components.MediaItemView
import com.example.peliculasserieskotlin.features.shared.components.HomeHeader
import com.example.peliculasserieskotlin.features.shared.components.InlineSearchTextField
import com.example.peliculasserieskotlin.features.shared.components.SearchFab
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Int, MediaType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val searchText = viewModel.searchText

    val listState = rememberLazyGridState()

    val pagedItems = viewModel.pagedMediaItems.collectAsState().value.collectAsLazyPagingItems()
    val favoriteItems by viewModel.favoriteMediaItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToDetail.collectLatest { (id, type) ->
            onNavigateToDetail(id, type)
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
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    val targetMediaType = if (selectedCategory == "Películas") MediaType.MOVIE else MediaType.SERIES

    Box(Modifier.fillMaxSize()) {
        when {
            uiState.error != null -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            uiState.isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    AnimatedVisibility(
                        visible = showHeader,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        HomeHeader(
                            selectedCategory = selectedCategory,
                            onCategorySelected = viewModel::updateCategory,
                            searchText = searchText,
                            onSearchQueryChanged = viewModel::onSearchQueryChanged,
                            sortBy = sortBy,
                            onSortTypeSelected = viewModel::setSortType,
                            inlineSearchActive = inlineSearchActive
                        )
                    }

                    when {
                        uiState.isSearching -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        sortBy == HomeViewModel.SortType.FAVORITE -> {
                            val itemsToDisplay = favoriteItems.filter { it.type == targetMediaType }

                            if (itemsToDisplay.isEmpty()) {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay favoritos")
                                }
                            } else {
                                LazyVerticalGrid(
                                    state = listState,
                                    columns = GridCells.Adaptive(150.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 4.dp)
                                ) {
                                    items(
                                        items = itemsToDisplay,
                                        key = { it.id }
                                    ) { item ->
                                        val isFavorite by viewModel.isFavorite(item.id, item.type)
                                            .collectAsState(initial = false)

                                        MediaItemView(
                                            mediaItem = item,
                                            isFavorite = isFavorite,
                                            onFavoriteClick = { mediaItem, newFavoriteState ->
                                                viewModel.toggleFavorite(mediaItem, newFavoriteState)
                                            },
                                            onItemClick = { viewModel.onMediaItemSelected(it) } // NUEVO
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                state = listState,
                                columns = GridCells.Adaptive(150.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                            ) {
                                items(pagedItems.itemCount) { index ->
                                    val item = pagedItems[index]
                                    if (item != null && item.type == targetMediaType) {
                                        val isFavorite by viewModel.isFavorite(item.id, item.type)
                                            .collectAsState(initial = false)

                                        MediaItemView(
                                            mediaItem = item,
                                            isFavorite = isFavorite,
                                            onFavoriteClick = { mediaItem, newFavoriteState ->
                                                viewModel.toggleFavorite(mediaItem, newFavoriteState)
                                            },
                                            onItemClick = { viewModel.onMediaItemSelected(it) } // NUEVO
                                        )
                                    }
                                }

                                when (pagedItems.loadState.append) {
                                    is LoadState.Loading -> {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                            }
                                        }
                                    }

                                    is LoadState.Error -> {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "Error al cargar más elementos",
                                                    color = Color.Red,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            }

                            when (pagedItems.loadState.refresh) {
                                is LoadState.Loading -> {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                is LoadState.Error -> {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Error al cargar datos", color = Color.Red)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = inlineSearchActive,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .padding(top = 48.dp)
        ) {
            InlineSearchTextField(
                searchText = searchText,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                selectedCategory = selectedCategory
            )
        }

        AnimatedVisibility(
            visible = !showHeader && !inlineSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            SearchFab(
                onClick = { viewModel.showInlineSearch() }
            )
        }
    }
}
