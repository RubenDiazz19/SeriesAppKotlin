package com.example.peliculasserieskotlin.features.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente elegante y minimalista que muestra la categoría seleccionada
 * (Películas o Series) y permite cambiar entre ellas al hacer clic.
 *
 * @param selectedCategory Categoría actualmente seleccionada
 * @param onCategorySelected Callback que se invoca cuando se selecciona una nueva categoría
 */
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val backgroundAnimatedColor by animateColorAsState(
        targetValue = if (isPressed) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "background_color"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    isPressed = true
                    // Cambiar directamente entre "Películas" y "Series" al pulsar
                    val newCategory = if (selectedCategory == "Películas") "Series" else "Películas"
                    onCategorySelected(newCategory)
                    // Reset pressed state after a short delay
                    isPressed = false
                }
                .padding(horizontal = 24.dp, vertical = 12.dp),
            color = backgroundAnimatedColor,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = selectedCategory.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}