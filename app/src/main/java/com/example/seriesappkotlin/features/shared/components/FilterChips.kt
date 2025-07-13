package com.example.seriesappkotlin.features.shared.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(
    showFavoritesOnly: Boolean,
    showWatchedOnly: Boolean,
    isGuest: Boolean,
    onToggleFavorites: () -> Unit,
    onToggleWatched: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isGuest) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    onClick = onToggleFavorites,
                    label = { Text("Favoritos") },
                    selected = showFavoritesOnly,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFD700),
                        selectedLabelColor = Color.Black
                    )
                )
            }
            item {
                FilterChip(
                    onClick = onToggleWatched,
                    label = { Text("Vistas") },
                    selected = showWatchedOnly,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFD700),
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
    }
}