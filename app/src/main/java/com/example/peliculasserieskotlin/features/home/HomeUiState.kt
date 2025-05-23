package com.example.peliculasserieskotlin.features.home

import com.example.peliculasserieskotlin.core.model.MediaItem

data class HomeUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)
