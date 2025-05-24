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
import com.example.peliculasserieskotlin.core.model.MediaItem

@SuppressLint("DefaultLocale")
@Composable
fun MediaItemView(
    mediaItem: MediaItem,
    isFavorite: Boolean = false,
    onFavoriteClick: ((mediaItem: MediaItem, isFavorite: Boolean) -> Unit)? = null,
    onItemClick: ((mediaItem: MediaItem) -> Unit)? = null // NUEVO parámetro
) {
    var localFavorite by remember { mutableStateOf(isFavorite) }

    // Sincroniza localFavorite con la propiedad isFavorite cuando esta cambie externamente
    LaunchedEffect(isFavorite) {
        localFavorite = isFavorite
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(160.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable { onItemClick?.invoke(mediaItem) }, // Detectar pulsación
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = mediaItem.posterUrl),
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
                    Text(
                        text = "⭐ ${String.format("%.1f", mediaItem.voteAverage)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(
                        onClick = {
                            val newFavoriteState = !localFavorite
                            localFavorite = newFavoriteState
                            onFavoriteClick?.invoke(mediaItem, newFavoriteState)
                        },
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
        }

        Text(
            text = mediaItem.title,
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
