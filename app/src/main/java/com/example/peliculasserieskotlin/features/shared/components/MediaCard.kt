package com.example.peliculasserieskotlin.features.shared.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
// Reemplazar import con comodín por imports específicos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
// Reemplazar import con comodín por imports específicos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.shared.components.rememberFavoriteState
import android.util.Log

/**
 * Componente reutilizable para mostrar una tarjeta de media (película o serie)
 * con diferentes estilos y opciones.
 */
@Composable
fun MediaCard(
    mediaItem: MediaItem,
    isFavorite: Boolean = false,
    onFavoriteClick: ((mediaItem: MediaItem, isFavorite: Boolean) -> Unit)? = null,
    onItemClick: ((mediaItem: MediaItem) -> Unit)? = null,
    style: MediaCardStyle = MediaCardStyle.GRID,
    showRating: Boolean = true,
    showTitle: Boolean = true,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    Log.d("DEBUG", "[MediaCard] showFavoriteIcon: $showFavoriteIcon, item: ${mediaItem.title}")
    when (style) {
        MediaCardStyle.GRID -> GridMediaCard(
            mediaItem = mediaItem,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onItemClick = onItemClick,
            showRating = showRating,
            showTitle = showTitle,
            showFavoriteIcon = showFavoriteIcon,
            modifier = modifier
        )
        MediaCardStyle.ROW -> RowMediaCard(
            mediaItem = mediaItem,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onItemClick = onItemClick,
            showRating = showRating,
            showFavoriteIcon = showFavoriteIcon,
            modifier = modifier
        )
        MediaCardStyle.COMPACT -> CompactMediaCard(
            mediaItem = mediaItem,
            onItemClick = onItemClick,
            modifier = modifier
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun GridMediaCard(
    mediaItem: MediaItem,
    isFavorite: Boolean,
    onFavoriteClick: ((mediaItem: MediaItem, isFavorite: Boolean) -> Unit)?,
    onItemClick: ((mediaItem: MediaItem) -> Unit)?,
    showRating: Boolean,
    showTitle: Boolean,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = mediaItem,
        isFavorite = isFavorite,
        onFavoriteToggle = { item, fav -> onFavoriteClick?.invoke(item, fav) }
    )

    Column(
        modifier = modifier
            .padding(8.dp)
            .width(160.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable { onItemClick?.invoke(mediaItem) },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = mediaItem.posterUrl,
                    contentDescription = "Poster of ${mediaItem.title}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (showRating) {
                        Text(
                            text = "⭐ ${String.format("%.1f", mediaItem.voteAverage)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    if (showFavoriteIcon && onFavoriteClick != null) {
                        IconButton(
                            onClick = toggleFavorite,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (localFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (localFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                tint = if (localFavorite) Color.Red else Color.White
                            )
                        }
                    }
                }
                
                // Indicador de tipo (película o serie)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp)
                ) {
                    mediaItem.genres?.take(2)?.forEachIndexed { idx, genre ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = genre.name,
                                color = Color.White,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize.times(0.85f),
                                maxLines = 1
                            )
                        }
                        if (idx < 1) Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
        }

        if (showTitle) {
            Text(
                text = mediaItem.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun RowMediaCard(
    mediaItem: MediaItem,
    isFavorite: Boolean,
    onFavoriteClick: ((mediaItem: MediaItem, isFavorite: Boolean) -> Unit)?,
    onItemClick: ((mediaItem: MediaItem) -> Unit)?,
    showRating: Boolean,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = mediaItem,
        isFavorite = isFavorite,
        onFavoriteToggle = { item, fav -> onFavoriteClick?.invoke(item, fav) }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 4.dp)
            .clickable { onItemClick?.invoke(mediaItem) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Poster
            AsyncImage(
                model = mediaItem.posterUrl,
                contentDescription = "Poster of ${mediaItem.title}",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            
            // Información
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(8.dp),
            ) {
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = mediaItem.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showRating) {
                        Text(
                            text = "⭐ ${String.format("%.1f", mediaItem.voteAverage)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    // Etiquetas de géneros (máximo 2)
                    Row {
                        mediaItem.genres?.take(2)?.forEachIndexed { idx, genre ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = genre.name,
                                    color = Color.White,
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize.times(0.85f),
                                    maxLines = 1
                                )
                            }
                            if (idx < 1) Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }
            
            // Botón de favorito
            if (showFavoriteIcon && onFavoriteClick != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    IconButton(
                        onClick = toggleFavorite
                    ) {
                        Icon(
                            imageVector = if (localFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (localFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (localFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactMediaCard(
    mediaItem: MediaItem,
    onItemClick: ((mediaItem: MediaItem) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .height(150.dp)
            .padding(4.dp)
            .clickable { onItemClick?.invoke(mediaItem) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = mediaItem.posterUrl,
                contentDescription = "Poster of ${mediaItem.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Etiquetas de géneros (máximo 2)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 4.dp, top = 4.dp)
            ) {
                mediaItem.genres?.take(2)?.forEachIndexed { idx, genre ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = genre.name,
                            color = Color.White,
                            fontSize = MaterialTheme.typography.labelSmall.fontSize.times(0.85f),
                            maxLines = 1
                        )
                    }
                    if (idx < 1) Spacer(modifier = Modifier.width(12.dp))
                }
            }
            // Título en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(4.dp)
            ) {
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

enum class MediaCardStyle {
    GRID,    // Estilo cuadrícula para pantallas principales
    ROW,     // Estilo fila para listas
    COMPACT  // Estilo compacto para carruseles horizontales
}