package com.example.peliculasserieskotlin.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = selectedCategory,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier.clickable {
                // Cambiar directamente entre "Películas" y "Series" al pulsar
                val newCategory = if (selectedCategory == "Películas") "Series" else "Películas"
                onCategorySelected(newCategory)
            }
        )
    }
}
