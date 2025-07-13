package com.example.peliculasserieskotlin.features.home

import com.example.peliculasserieskotlin.core.model.Serie

data class HomeUiState(
    val mediaItems: List<Serie> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false,
    val showOfflineWarning: Boolean = false
)
