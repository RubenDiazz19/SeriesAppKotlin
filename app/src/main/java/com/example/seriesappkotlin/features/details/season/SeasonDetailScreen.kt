package com.example.seriesappkotlin.features.details.season

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.runtime.derivedStateOf
import com.example.seriesappkotlin.core.model.Episode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.key
import androidx.compose.foundation.shape.CircleShape
import com.example.seriesappkotlin.features.favorites.FavoriteViewModel
import com.example.seriesappkotlin.features.favorites.WatchedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDetailScreen(
    serieId: Int,
    seasonNumber: Int,
    onBackClick: () -> Unit,
    viewModel: SeasonDetailViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    watchedViewModel: WatchedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serieId, seasonNumber) {
        viewModel.loadSeasonDetails(serieId, seasonNumber)
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
                SeasonDetailContent(
                    uiState = state,
                    watchedViewModel = watchedViewModel,
                    serieId = serieId,
                    seasonNumber = seasonNumber
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

        // Top bar con botones de navegación y visto
        TopAppBar(
            title = { /* No title here */ },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver", 
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            actions = {
                val isUserLoggedIn = favoriteViewModel.isUserLoggedIn()
                
                if (isUserLoggedIn && uiState != null) {
                    // Indicador de visto para toda la temporada (no pulsable)
                    val isWatched by watchedViewModel.isSeasonWatched(serieId, seasonNumber).collectAsState(initial = false)
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWatched) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (isWatched) "Temporada vista" else "Temporada no vista",
                            tint = if (isWatched) Color(0xFFFFD700) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SeasonDetailContent(
    uiState: SeasonDetailUiState,
    watchedViewModel: WatchedViewModel,
    serieId: Int,
    seasonNumber: Int,
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
                .height(450.dp) // Misma altura que SerieDetailScreen
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Poster de ${uiState.name}",
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
            // Título de la temporada
            Text(
                text = uiState.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Información de temporada y episodios
            uiState.episodes?.let { episodes ->
                Text(
                    text = "Temporada $seasonNumber • ${episodes.size} episodios",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Fecha de estreno si está disponible
            uiState.airDate?.let { airDate ->
                Text(
                    text = "Fecha de estreno: $airDate",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
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
            
            
            
            // Luego, reemplazar la sección de episodios (líneas 227-295) con esta versión corregida:
            
            // Episodios
            uiState.episodes?.let { episodes ->
                if (episodes.isNotEmpty()) {
                    // Estado para forzar recomposición cuando cambian los episodios
                    var refreshTrigger by remember(serieId, seasonNumber) { mutableStateOf(0) }
                    
                    // Calcular si todos están marcados como vistos
                    val allWatched = remember(serieId, seasonNumber, episodes.map { it.isWatched }) {
                        episodes.all { it.isWatched }
                    }
                    
                    // LaunchedEffect que se ejecuta solo cuando cambian los episodios reales
                    LaunchedEffect(serieId, seasonNumber, episodes.map { it.isWatched }) {
                        val currentAllWatched = episodes.all { it.isWatched }
                        watchedViewModel.toggleWatchedSeason(serieId, seasonNumber, currentAllWatched)
                    }
                    
                    // Título y botón de marcar todos en la misma línea
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Episodios",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        // Botón que se actualiza dinámicamente
                        Button(
                            onClick = {
                                val newWatchedState = !allWatched
                                episodes.forEach { episode ->
                                    episode.isWatched = newWatchedState
                                    watchedViewModel.toggleWatchedEpisode(serieId, seasonNumber, episode.episodeNumber, newWatchedState)
                                }
                                refreshTrigger++ // Forzar recomposición
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .height(40.dp)
                                .widthIn(min = 200.dp)
                        ) {
                            Text(
                                text = if (allWatched) "Desmarcar todo como visto" else "Marcar todo como visto",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    // Lista de episodios con key para forzar recomposición
                    episodes.forEachIndexed { index, episode ->
                        key("$serieId-$seasonNumber-$index-$refreshTrigger") {
                            EpisodeItem(
                                episode = episode,
                                watchedViewModel = watchedViewModel,
                                serieId = serieId,
                                seasonNumber = seasonNumber,
                                allEpisodes = episodes,
                                onEpisodeStateChanged = { refreshTrigger++ }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}



@Composable
private fun EpisodeItem(
    episode: Episode,
    watchedViewModel: WatchedViewModel,
    serieId: Int,
    seasonNumber: Int,
    allEpisodes: List<Episode>,
    onEpisodeStateChanged: () -> Unit // Nuevo parámetro
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isWatched by remember(episode.isWatched) { mutableStateOf(episode.isWatched) }
    val maxLines = if (isExpanded) Int.MAX_VALUE else 2
    
    // Sincronizar el estado local con el del episodio cuando cambie externamente
    LaunchedEffect(episode.isWatched) {
        isWatched = episode.isWatched
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna izquierda: Contenido del episodio
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.name}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sinopsis con funcionalidad de expandir/contraer
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Botón "Ver más" / "Ver menos" solo si el texto es largo
                if (episode.overview.length > 100) {
                    Text(
                        text = if (isExpanded) "Ver menos" else "... Ver más",
                        color = Color(0xFFFFD700),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { isExpanded = !isExpanded }
                    )
                }
            }
            
            // Columna derecha: Botón de visto/no visto centrado
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { 
                        isWatched = !isWatched
                        episode.isWatched = isWatched
                        watchedViewModel.toggleWatchedEpisode(serieId, seasonNumber, episode.episodeNumber, isWatched)
                        onEpisodeStateChanged() // Notificar el cambio
                    }
                ) {
                    Icon(
                        imageVector = if (isWatched) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = if (isWatched) "Marcar como no visto" else "Marcar como visto",
                        tint = if (isWatched) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
