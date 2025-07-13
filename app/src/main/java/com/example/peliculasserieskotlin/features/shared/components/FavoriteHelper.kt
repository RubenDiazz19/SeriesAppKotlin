package com.example.peliculasserieskotlin.features.shared.components

import androidx.compose.runtime.*
import com.example.peliculasserieskotlin.core.model.Serie

/**
 * Helper centralizado para manejar el estado de favorito y la acción de toggle.
 * Devuelve el estado local y una función para alternar el favorito.
 */
@Composable
fun rememberFavoriteState(
    mediaItem: Serie,
    isFavorite: Boolean,
    onFavoriteToggle: (Serie, Boolean) -> Unit
): Pair<Boolean, () -> Unit> {
    var localFavorite by remember { mutableStateOf(isFavorite) }

    // Sincroniza localFavorite con la propiedad isFavorite cuando esta cambie externamente
    LaunchedEffect(isFavorite) {
        localFavorite = isFavorite
    }

    val toggleFavorite = {
        val newFavoriteState = !localFavorite
        localFavorite = newFavoriteState
        onFavoriteToggle(mediaItem, newFavoriteState)
    }

    return Pair(localFavorite, toggleFavorite)
}