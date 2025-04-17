package com.example.peliculasserieskotlin.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.peliculasserieskotlin.domain.model.Series

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(series: Series) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = series.name) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Año: ${series.year}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Puntuación: ${series.voteAverage}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Descripción: ${series.overview}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Poster: ${series.posterUrl}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
