package com.example.peliculasserieskotlin.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
// import androidx.compose.foundation.border // Unused
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
// import androidx.compose.foundation.shape.CircleShape // Unused
// import androidx.compose.foundation.shape.RoundedCornerShape // Unused
// import androidx.compose.foundation.text.KeyboardOptions // Unused
import androidx.compose.material.icons.Icons // Used by components, but not directly here for specific icons
// import androidx.compose.material.icons.filled.Edit // Unused
// import androidx.compose.material.icons.filled.Favorite // Unused
// import androidx.compose.material.icons.filled.Search // Unused
// import androidx.compose.material.icons.filled.Star // Unused
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.text.input.ImeAction // Unused
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.peliculasserieskotlin.domain.model.MediaType
// import com.example.peliculasserieskotlin.presentation.components.CategoryDropdown // Commented out, can be removed
import com.example.peliculasserieskotlin.presentation.components.MediaItemView
import com.example.peliculasserieskotlin.presentation.components.HomeHeader
import com.example.peliculasserieskotlin.presentation.components.InlineSearchTextField
import com.example.peliculasserieskotlin.presentation.components.SearchFab


@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val inlineSearchActive by viewModel.inlineSearchActive.collectAsState()
    val showSearchBarForced by viewModel.showSearchBarForced.collectAsState()
    val searchText = viewModel.searchText

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

    val showHeader by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&listState.firstVisibleItemScrollOffset == 0
        }
    }

    val targetMediaType = if (selectedCategory == "Películas") MediaType.MOVIE else MediaType.SERIES
    val itemsToDisplay = uiState.mediaItems.filter { it.type == targetMediaType }

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
            /*####################################################################*/
            /* -----------------------  CABECERA  ------------------------------- */
            /*####################################################################*/
            AnimatedVisibility(
                // Solo muestra la cabecera si se está en la parte superior de la lista
                visible = showHeader,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                //Insertamos la cabecera
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

            /*#####################################################################*/
            /* -----------------------  CONTENIDO  ------------------------------- */
            /*#####################################################################*/
            when {
                // Si se está buscando o cargando, muestra un indicador de progreso
                uiState.isSearching || uiState.isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Mostrar el mensaje de error
                        Text("Error: ${uiState.error}", color = Color.Red)
                    }
                }
                //Si no hay resultados, muestra un mensaje
                itemsToDisplay.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay resultados")
                    }
                }
                /*#####################################################################*/
                /* -----------------------  LISTADO  ------------------------------- */
                /*#####################################################################*/
                else -> {
                    LazyVerticalGrid(
                        state = listState,
                        columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp)
                    ) {
                        items(itemsToDisplay) { item ->
                            // Observar el estado de favorito para cada elemento
                            val isFavorite by viewModel.isFavorite(item.id, item.type)
                                .collectAsState(initial = false)
                            
                            MediaItemView(
                                mediaItem = item,
                                isFavorite = isFavorite,
                                onFavoriteClick = { mediaItem, newFavoriteState ->
                                    viewModel.toggleFavorite(mediaItem, newFavoriteState)
                                }
                            )
                        }
                    }
                }
            }
        }

        /*####################################################################*/
        /* -----------------------  BUSCADOR  ------------------------------- */
        /*####################################################################*/
        AnimatedVisibility(
            visible = inlineSearchActive,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .padding(top = 48.dp) // Ajusta este padding según sea necesario después de mover la cabecera
        ) {
            InlineSearchTextField(
                searchText = searchText,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                selectedCategory = selectedCategory
            )
        }

        /*#########################################################################*/
        /* -----------------------  FLOATING BUTTON  ----------------------------- */
        /*#########################################################################*/
        AnimatedVisibility(
            visible = !showHeader && !inlineSearchActive,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // Este padding ya estaba, mantenlo o ajústalo
        ) {
            SearchFab(
                onClick = { viewModel.showInlineSearch() }
                // El modifier para alinear y padding se aplica en AnimatedVisibility
            )
        }
    }
}


