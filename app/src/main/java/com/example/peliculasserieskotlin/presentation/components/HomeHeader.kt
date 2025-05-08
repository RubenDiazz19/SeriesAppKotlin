package com.example.peliculasserieskotlin.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel // Asegúrate que esta ruta es correcta

@Composable
fun HomeHeader(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchText: String,
    onSearchQueryChanged: (String) -> Unit,
    sortBy: HomeViewModel.SortType,
    onSortTypeSelected: (HomeViewModel.SortType) -> Unit,
    inlineSearchActive: Boolean
) {
    Column(
        modifier = Modifier.padding(top = 24.dp)
    ) {
        // Dropdown centrado
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            CategoryDropdown(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )
        }
        // Buscador de cabecera
        if (!inlineSearchActive) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Buscar...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        // Botones de orden
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { onSortTypeSelected(HomeViewModel.SortType.RATING) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "★",
                    tint = if (sortBy == HomeViewModel.SortType.RATING)
                        Color(0xFFF4C10F) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(2.dp))
            IconButton(
                onClick = { onSortTypeSelected(HomeViewModel.SortType.ALPHABETIC) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "A–Z",
                    tint = if (sortBy == HomeViewModel.SortType.ALPHABETIC)
                        Color.Blue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(2.dp))
            IconButton(
                onClick = { onSortTypeSelected(HomeViewModel.SortType.FAVORITE) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "❤️",
                    tint = if (sortBy == HomeViewModel.SortType.FAVORITE)
                        Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}