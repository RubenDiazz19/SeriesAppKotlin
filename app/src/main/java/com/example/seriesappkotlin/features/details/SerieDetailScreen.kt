
package com.example.seriesappkotlin.features.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seriesappkotlin.core.model.Season
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.favorites.WatchedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerieDetailScreen(
    serieId: Int,
    onBackClick: () -> Unit,
    viewModel: SerieDetailViewModel = hiltViewModel(),
    watchedViewModel: WatchedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serieId) {
        viewModel.loadDetail(serieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        uiState?.let { state ->
            if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val isWatched by watchedViewModel.isWatched(serieId).collectAsState(initial = false)
                val serie = Serie(
                    id = serieId,
                    title = state.title ?: "",
                    overview = state.overview ?: "",
                    posterUrl = state.posterUrl ?: "",
                    backdropUrl = null,
                    voteAverage = 0.0,
                    genres = state.genres,
                    seasons = emptyList(),
                    originalTitle = state.originalTitle,
                    firstAirDate = state.releaseDate,
                    voteCount = null,
                    runtime = null,
                    numberOfSeasons = state.numberOfSeasons,
                    numberOfEpisodes = state.numberOfEpisodes,
                    status = state.status,
                    tagline = state.tagline
                )
                SerieDetailContent(
                    uiState = state,
                    isWatched = isWatched,
                    onWatchedClick = { newWatchedState ->
                        watchedViewModel.toggleWatched(serie, newWatchedState)
                    },
                    modifier = Modifier.padding(paddingValues),
                    onSeasonClick = { seasonId ->
                        // TODO: Navigate to season detail
                    }
                )
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun SerieDetailContent(
    uiState: SerieDetailUiState,
    isWatched: Boolean,
    onWatchedClick: (Boolean) -> Unit,
    onSeasonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen de fondo con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Poster de ${uiState.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradiente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            // Botón de favorito
            FloatingActionButton(
                onClick = { onWatchedClick(!isWatched) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isWatched) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isWatched) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isWatched) Color.Red else Color.White
                )
            }
        }
        
        // Contenido de detalles
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título
            Text(
                text = uiState.title ?: "Sin título",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Tagline
            uiState.tagline?.let { tagline ->
                if (tagline.isNotBlank()) {
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información adicional
            InfoRow("Fecha de estreno", uiState.releaseDate)
            InfoRow("Calificación", uiState.voteAverageFormatted)
            uiState.runtimeFormatted?.let { InfoRow("Duración", it) }
            uiState.numberOfSeasons?.let { InfoRow("Temporadas", it.toString()) }
            uiState.numberOfEpisodes?.let { InfoRow("Episodios", it.toString()) }
            InfoRow("Estado", uiState.status)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Géneros
            uiState.genres?.let { genres ->
                if (genres.isNotEmpty()) {
                    Text(
                        text = "Géneros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items(genres) { genre ->
                            Card(
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Sinopsis
            uiState.overview?.let { overview ->
                if (overview.isNotBlank()) {
                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = overview,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 20.sp
                    )
                }
            }
            
            // Seasons Section
            uiState.seasons?.let { seasons ->
                if (seasons.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SeasonsSection(seasons = seasons, onSeasonClick = onSeasonClick)
                }
            }
        }
    }
}

@Composable
private fun SeasonsSection(
    seasons: List<Season>,
    onSeasonClick: (Int) -> Unit
) {
    Column {
        Text(
            text = "Temporadas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(seasons) { season ->
                SeasonCard(season = season, onClick = { onSeasonClick(season.id) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonCard(
    season: Season,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(220.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(season.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = season.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = season.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${season.episodeCount} episodios",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    value?.let {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
