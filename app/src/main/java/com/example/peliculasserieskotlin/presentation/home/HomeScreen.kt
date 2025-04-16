package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.peliculasserieskotlin.domain.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Películas Populares") }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.movies) { movie ->
                MovieItemCard(movie = movie) {
                    onMovieClick(movie)
                }
            }
        }
    }
}
