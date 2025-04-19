package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.domain.model.Series
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val series by viewModel.series.collectAsState()

    val listState = rememberLazyGridState()

    Column(modifier = Modifier.fillMaxSize()) {

        // Espaciado visual antes de la lista
        Spacer(modifier = Modifier.height(8.dp))

        // Grid con scroll infinito
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Colocar el CategoryDropdown justo encima de las tarjetas
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.updateCategory(it) }
                )
            }

            if (selectedCategory == "Películas") {
                items(movies) { movie ->
                    MovieSeriesItem(
                        title = movie.title,
                        imageUrl = movie.posterUrl,
                        rating = movie.voteAverage
                    )
                }
            } else {
                items(series) { serie ->
                    MovieSeriesItem(
                        title = serie.name,
                        imageUrl = serie.posterUrl,
                        rating = serie.voteAverage
                    )
                }
            }

        }


        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                lastVisible >= totalItems - 3
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value) {
                viewModel.loadNextPage()
            }
        }
    }
}