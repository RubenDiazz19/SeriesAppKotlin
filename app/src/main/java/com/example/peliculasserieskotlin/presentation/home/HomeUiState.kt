package com.example.peliculasserieskotlin.presentation.home

import com.example.peliculasserieskotlin.domain.model.MediaItem

data class HomeUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)
