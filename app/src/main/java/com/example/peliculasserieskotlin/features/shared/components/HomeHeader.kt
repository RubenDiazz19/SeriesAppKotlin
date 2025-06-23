package com.example.peliculasserieskotlin.features.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peliculasserieskotlin.features.home.HomeViewModel

/**
 * Cabecera mejorada de la pantalla principal con diseño elegante y minimalista
 */
@Composable
fun HomeHeader(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchText: String,
    onSearchQueryChanged: (String) -> Unit,
    sortBy: HomeViewModel.SortType,
    onSortTypeSelected: (HomeViewModel.SortType) -> Unit,
    inlineSearchActive: Boolean,
    showFavoriteSort: Boolean = true,
    isGuest: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 16.dp)
    ) {
        // Título de categoría elegante
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        val newCategory = if (selectedCategory == "Películas") "Series" else "Películas"
                        onCategorySelected(newCategory)
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                color = Color.Transparent
            ) {
                Text(
                    text = selectedCategory.uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Light,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Campo de búsqueda minimalista
        if (!inlineSearchActive) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                tonalElevation = 0.dp
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchQueryChanged,
                    placeholder = {
                        Text(
                            text = "Buscar ${selectedCategory.lowercase()}...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }

        // Controles de ordenación elegantes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spacer para empujar los botones hacia la derecha
            Spacer(modifier = Modifier.weight(1f))

            SortButton(
                isSelected = sortBy == HomeViewModel.SortType.RATING,
                icon = Icons.Default.Star,
                contentDescription = "Ordenar por puntuación",
                selectedColor = Color(0xFFF4C10F),
                onClick = { onSortTypeSelected(HomeViewModel.SortType.RATING) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            SortButton(
                isSelected = sortBy == HomeViewModel.SortType.ALPHABETIC,
                icon = Icons.Default.Edit,
                contentDescription = "Ordenar alfabéticamente",
                selectedColor = MaterialTheme.colorScheme.primary,
                onClick = { onSortTypeSelected(HomeViewModel.SortType.ALPHABETIC) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Solo mostrar el botón de favoritos si no es invitado y showFavoriteSort es true
            if (showFavoriteSort && !isGuest) {
                SortButton(
                    isSelected = sortBy == HomeViewModel.SortType.FAVORITE,
                    icon = Icons.Default.Favorite,
                    contentDescription = "Ver favoritos",
                    selectedColor = Color(0xFFE91E63),
                    onClick = { onSortTypeSelected(HomeViewModel.SortType.FAVORITE) }
                )
            }
        }
    }
}

@Composable
private fun SortButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) {
            selectedColor.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}