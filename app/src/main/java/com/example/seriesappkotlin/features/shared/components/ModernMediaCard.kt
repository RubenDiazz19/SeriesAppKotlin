package com.example.seriesappkotlin.features.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.CheckCircle // Cambiar import
import androidx.compose.material.icons.outlined.CheckCircle // Cambiar import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.seriesappkotlin.core.model.Serie

@Composable
fun ModernMediaCard(
    serie: Serie,
    isFavorite: Boolean = false,
    isWatched: Boolean = false, // Agregar parámetro
    onFavoriteClick: ((serie: Serie, isFavorite: Boolean) -> Unit)? = null,
    onWatchedClick: ((serie: Serie, isWatched: Boolean) -> Unit)? = null, // Agregar parámetro
    onItemClick: ((serie: Serie) -> Unit)? = null,
    showFavoriteIcon: Boolean = true,
    showWatchedIcon: Boolean = true, // Agregar parámetro
    modifier: Modifier = Modifier
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = serie,
        isFavorite = isFavorite,
        onFavoriteToggle = { item, fav -> onFavoriteClick?.invoke(item, fav) }
    )
    
    // Agregar estado local para watched
    var localWatched by remember { mutableStateOf(isWatched) }
    
    // Sincronizar estado local con prop
    LaunchedEffect(isWatched) {
        localWatched = isWatched
    }
    
    val toggleWatched: () -> Unit = {
        val newWatchedState = !localWatched
        localWatched = newWatchedState
        onWatchedClick?.invoke(serie, newWatchedState)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f) // Proporción similar a los posters de la imagen
            .clickable { onItemClick?.invoke(serie) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = serie.posterUrl,
                contentDescription = "Poster of ${serie.title}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Subtle black overlay for better rating visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )

            // Overlay gradient for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Rating in top-left corner
            Text(
                text = "⭐ ${String.format("%.1f", serie.voteAverage)}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
            
            // Iconos en la esquina superior derecha
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Icono de favorito (primero)
                if (showFavoriteIcon && onFavoriteClick != null) {
                    IconButton(
                        onClick = toggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (localFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (localFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (localFavorite) Color.Red else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Icono de visto (segundo)
                if (showWatchedIcon && onWatchedClick != null) {
                    IconButton(
                        onClick = toggleWatched,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (localWatched) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (localWatched) "Marcar como no vista" else "Marcar como vista",
                            tint = if (localWatched) Color(0xFFFFD700) else Color.White, // Amarillo cuando está marcada
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Title at the bottom
            Text(
                text = serie.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}