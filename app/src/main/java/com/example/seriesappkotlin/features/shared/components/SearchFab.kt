package com.example.seriesappkotlin.features.shared.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Botón flotante de acción (FAB) para iniciar una búsqueda.
 * Diseñado con un icono de lupa y estilo personalizado.
 *
 * @param onClick Callback cuando se hace clic en el botón
 * @param modifier Modificador opcional para personalizar el componente
 */
@Composable
fun SearchFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        elevation = FloatingActionButtonDefaults.elevation(6.dp),
        modifier = modifier
            .size(50.dp)
            .border(1.dp, Color.Black, CircleShape)
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = "🔍",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}