package com.example.peliculasserieskotlin.features.shared.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.peliculasserieskotlin.core.model.Serie
import com.example.peliculasserieskotlin.features.shared.components.rememberFavoriteState

@SuppressLint("DefaultLocale")
@Composable
fun MediaItemView(
    serie: Serie,
    isFavorite: Boolean = false,
    onFavoriteClick: ((serie: Serie, isFavorite: Boolean) -> Unit)? = null,
    onItemClick: ((serie: Serie) -> Unit)? = null
) {
    val (localFavorite, toggleFavorite) = rememberFavoriteState(
        mediaItem = serie,
        isFavorite = isFavorite,
        onFavoriteToggle = { item, fav -> onFavoriteClick?.invoke(item, fav) }
    )

    Column(
        modifier = Modifier
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
                Image(
                    painter = rememberAsyncImagePainter(model = serie.posterUrl),
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
                    Text(
                        text = "⭐ ${String.format("%.1f", serie.voteAverage)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
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

                // Etiquetas de géneros
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

        Text(
            text = serie.title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
