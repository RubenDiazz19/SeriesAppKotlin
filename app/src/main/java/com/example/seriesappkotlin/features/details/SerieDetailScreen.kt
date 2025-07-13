
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
import androidx.compose.material.icons.filled.Star
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
    onSeasonClick: (Int, Int) -> Unit, // Modified to pass serieId and seasonNumber
    viewModel: SerieDetailViewModel = hiltViewModel(),
    watchedViewModel: WatchedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serieId) {
        viewModel.loadDetail(serieId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        uiState?.let { state ->
            if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                SerieDetailContent(
                    uiState = state,
                    onSeasonClick = { seasonNumber ->
                        onSeasonClick(serieId, seasonNumber)
                    }
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Top bar con botones de navegación y favoritos
        TopAppBar(
            title = { /* No title here */ },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            },
            actions = {
                val isWatched by watchedViewModel.isWatched(serieId).collectAsState(initial = false)
                IconButton(onClick = { 
                    val serie = Serie(
                        id = serieId,
                        title = uiState?.title ?: "",
                        overview = uiState?.overview ?: "",
                        posterUrl = uiState?.posterUrl ?: "",
                        backdropUrl = null,
                        voteAverage = 0.0,
                        genres = uiState?.genres,
                        seasons = emptyList(),
                        originalTitle = uiState?.originalTitle,
                        firstAirDate = uiState?.releaseDate,
                        voteCount = null,
                        runtime = null,
                        numberOfSeasons = uiState?.numberOfSeasons,
                        numberOfEpisodes = uiState?.numberOfEpisodes,
                        status = uiState?.status,
                        tagline = uiState?.tagline
                    )
                    watchedViewModel.toggleWatched(serie, !isWatched) 
                }) {
                    Icon(
                        imageVector = if (isWatched) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isWatched) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isWatched) Color.Red else Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SerieDetailContent(
    uiState: SerieDetailUiState,
    onSeasonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen de fondo que ocupa toda la parte superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp) // Aumentar altura para un look más inmersivo
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
            
            // Gradiente para que el texto debajo sea legible
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 600f
                        )
                    )
            )
        }
        
        // Contenido de detalles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black) // Fondo negro para el contenido
        ) {
            // Calificación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Calificación", tint = Color(0xFFFFD700))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = uiState.voteAverageFormatted,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título y tagline
            Text(
                text = uiState.title ?: "Sin título",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            uiState.tagline?.let { tagline ->
                if (tagline.isNotBlank()) {
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Sinopsis
            uiState.overview?.let { overview ->
                if (overview.isNotBlank()) {
                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = overview,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 24.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Géneros
            uiState.genres?.let { genres ->
                if (genres.isNotEmpty()) {
                    Text(
                        text = "Géneros",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        items(genres) { genre ->
                            Card(
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFD700)
                                )
                            ) {
                                Text(
                                    text = genre.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
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
private fun InfoPill(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    value?.let {
        Card(
            modifier = modifier.padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$label: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
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
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(seasons) { season ->
                SeasonCard(season = season, onClick = { onSeasonClick(season.seasonNumber) }) // Pass seasonNumber instead of id
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
            .height(240.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
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
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(
                    text = season.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${season.episodeCount} episodios",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
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
