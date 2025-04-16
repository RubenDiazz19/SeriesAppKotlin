package com.example.peliculasserieskotlin.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.peliculasserieskotlin.domain.Movie

@Composable
fun MovieItemCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    )
    {
        Column {
            Image(
                painter = rememberAsyncImagePainter(movie.posterUrl),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = "Año: ${movie.year}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
