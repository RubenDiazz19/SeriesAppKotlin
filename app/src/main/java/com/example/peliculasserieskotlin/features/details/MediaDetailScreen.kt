package com.example.peliculasserieskotlin.features.details

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.features.favorites.FavoriteViewModel
import com.example.peliculasserieskotlin.features.shared.components.LoadingState
import com.example.peliculasserieskotlin.features.shared.components.ErrorState
import kotlin.getValue

/**
 * Pantalla unificada para mostrar el detalle de una película o serie.
 * Rediseñada con estilo Netflix/Disney para una experiencia más elegante.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailScreen(
    mediaId: Int,
    type: MediaType,
    isGuest: Boolean,
    viewModel: MediaDetailViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by favoriteViewModel.isFavorite(mediaId, type).collectAsState(initial = false)

    LaunchedEffect(mediaId, type) {
        viewModel.loadDetail(mediaId, type)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when {
            uiState?.error != null -> {
                ErrorState(message = uiState!!.error ?: "Error desconocido")
            }
            uiState == null -> {
                LoadingState()
            }
            else -> {
                MediaDetailContent(
                    uiState = uiState!!,
                    isFavorite = isFavorite,
                    onFavoriteClick = { newFavoriteState ->
                        // Crear MediaItem a partir del uiState
                        val mediaItem = MediaItem(
                            id = mediaId,
                            title = uiState!!.title,
                            posterUrl = uiState!!.posterUrl.toString(),
                            type = type,
                            overview = uiState!!.overview,
                            voteAverage = extractVoteAverage(uiState!!.voteAverageFormatted),
                            backdropUrl = uiState!!.posterUrl.toString()
                        )
                        favoriteViewModel.toggleFavorite(mediaItem, newFavoriteState)
                    }
                )
            }
        }

        // Botón de retroceso flotante
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 16.dp, top = 60.dp)
                .size(40.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        // Botón de favorito flotante (arriba a la derecha)
        if (!isGuest) {
            IconButton(
                onClick = {
                    val mediaItem = MediaItem(
                        id = mediaId,
                        title = uiState?.title ?: "",
                        posterUrl = uiState?.posterUrl ?: "",
                        type = type,
                        overview = uiState?.overview ?: "",
                        voteAverage = extractVoteAverage(uiState?.voteAverageFormatted),
                        backdropUrl = uiState?.posterUrl ?: ""
                    )
                    favoriteViewModel.toggleFavorite(mediaItem, !isFavorite)
                },
                modifier = Modifier
                    .padding(end = 16.dp, top = 60.dp)
                    .size(40.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MediaDetailContent(
    uiState: MediaDetailUiState,
    isFavorite: Boolean = false,
    onFavoriteClick: (Boolean) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section con imagen de fondo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            // Imagen de fondo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradiente oscuro para mejor legibilidad
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Contenido superpuesto
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                uiState.tagline?.takeIf { it.isNotEmpty() }?.let { tagline ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating y año
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiState.voteAverageFormatted ?: "N/A",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            lineHeight = 20.sp
                        )
                    }

                    uiState.releaseDate?.let { date ->
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    uiState.runtimeFormatted?.let { runtime ->
                        Text(
                            text = runtime,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Sección de contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Descripción
            Text(
                text = uiState.overview,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Justify
            )

            // Géneros
            uiState.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        ModernGenreChip(genre = genre)
                    }
                }
            }

            // Información adicional en cards elegantes
            if (hasAdditionalInfo(uiState)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    InfoGrid(uiState = uiState)
                }
            }
        }
    }
}

@Composable
fun ModernGenreChip(genre: GenreItem) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Text(
            text = genre.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun InfoGrid(uiState: MediaDetailUiState) {
    val infoItems = buildList {
        uiState.originalTitle?.let { add("Título Original" to it) }
        uiState.releaseDate?.let { add("Estreno" to it) }
        uiState.status?.let { add("Estado" to it) }
        uiState.budgetFormatted?.let { add("Presupuesto" to it) }
        uiState.revenueFormatted?.let { add("Recaudación" to it) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        infoItems.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { (label, value) ->
                    ModernInfoCard(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Rellena el espacio si solo hay un elemento en la fila
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ModernInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun hasAdditionalInfo(uiState: MediaDetailUiState): Boolean {
    return listOfNotNull(
        uiState.originalTitle,
        uiState.releaseDate,
        uiState.status,
        uiState.budgetFormatted,
        uiState.revenueFormatted
    ).isNotEmpty()
}

// Función helper para extraer el voteAverage del string formateado
private fun extractVoteAverage(voteAverageFormatted: String?): Double {
    return voteAverageFormatted?.let { formatted ->
        try {
            // Extraer el número antes del " / 10"
            formatted.substringBefore(" / 10").toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    } ?: 0.0
}
