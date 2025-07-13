package com.example.seriesappkotlin.features.shared.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
// Reemplazar import con comodín por imports específicos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.seriesappkotlin.core.model.Serie
import android.util.Log

/**
 * Componente reutilizable para mostrar una tarjeta de media (película o serie)
 * con diferentes estilos y opciones.
 */
@Composable
fun MediaCard(
    serie: Serie,
    isFavorite: Boolean = false,
    onFavoriteClick: ((serie: Serie, isFavorite: Boolean) -> Unit)? = null,
    onItemClick: ((serie: Serie) -> Unit)? = null,
    style: MediaCardStyle = MediaCardStyle.GRID,
    showRating: Boolean = true,
    showTitle: Boolean = true,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    Log.d("DEBUG", "[MediaCard] showFavoriteIcon: $showFavoriteIcon, item: ${serie.title}")
    when (style) {
        MediaCardStyle.GRID -> GridMediaCard(
            serie = serie,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onItemClick = onItemClick,
            showRating = showRating,
            showTitle = showTitle,
            showFavoriteIcon = showFavoriteIcon,
            modifier = modifier
        )
        MediaCardStyle.ROW -> RowMediaCard(
            serie = serie,
            isFavorite = isFavorite,
            onFavoriteClick = onFavoriteClick,
            onItemClick = onItemClick,
            showRating = showRating,
            showFavoriteIcon = showFavoriteIcon,
            modifier = modifier
        )
        MediaCardStyle.COMPACT -> CompactMediaCard(
            serie = serie,
            onItemClick = onItemClick,
            modifier = modifier
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun GridMediaCard(
    serie: Serie,
    isFavorite: Boolean,
    onFavoriteClick: ((serie: Serie, isFavorite: Boolean) -> Unit)?,
    onItemClick: ((serie: Serie) -> Unit)?,
    showRating: Boolean,
    showTitle: Boolean,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = serie,
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
                .clickable { onItemClick?.invoke(serie) },
            shape = RoundedCornerShape(12.dp),
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
                            text = "⭐ ${String.format("%.1f", serie.voteAverage)}",
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
                                imageVector = if (localFavorite) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = if (localFavorite) "Quitar de vistos" else "Añadir a vistos",
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
                    serie.genres?.take(2)?.forEachIndexed { idx, genre ->
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
                text = serie.title,
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
    serie: Serie,
    isFavorite: Boolean,
    onFavoriteClick: ((serie: Serie, isFavorite: Boolean) -> Unit)?,
    onItemClick: ((serie: Serie) -> Unit)?,
    showRating: Boolean,
    showFavoriteIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = serie,
        isFavorite = isFavorite,
        onFavoriteToggle = { item, fav -> onFavoriteClick?.invoke(item, fav) }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 4.dp)
            .clickable { onItemClick?.invoke(serie) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Poster
            AsyncImage(
                model = serie.posterUrl,
                contentDescription = "Poster of ${serie.title}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
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
                    text = serie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = serie.overview,
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
                            text = "⭐ ${String.format("%.1f", serie.voteAverage)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    // Etiquetas de géneros (máximo 2)
                    Row {
                        serie.genres?.take(2)?.forEachIndexed { idx, genre ->
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
                            imageVector = if (localFavorite) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (localFavorite) "Quitar de vistos" else "Añadir a vistos",
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
    serie: Serie,
    onItemClick: ((serie: Serie) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(100.dp)
            .height(150.dp)
            .padding(4.dp)
            .clickable { onItemClick?.invoke(serie) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = serie.posterUrl,
                contentDescription = "Poster of ${serie.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Etiquetas de géneros (máximo 2)
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 4.dp, top = 4.dp)
            ) {
                serie.genres?.take(2)?.forEachIndexed { idx, genre ->
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
                    text = serie.title,
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