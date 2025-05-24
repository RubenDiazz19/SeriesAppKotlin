package com.example.peliculasserieskotlin.features.details

import com.example.peliculasserieskotlin.core.model.GenreItem

// Clase de estado para la UI, conteniendo datos listos para mostrar
data class MediaDetailUiState(
    val title: String = "",
    val tagline: String? = null,
    val overview: String = "",
    val posterUrl: String? = null,
    val originalTitle: String? = null,
    val releaseDate: String? = null,
    val voteAverageFormatted: String = "",
    val runtimeFormatted: String? = null,
    val budgetFormatted: String? = null,
    val revenueFormatted: String? = null,
    val status: String? = null,
    val genres: List<GenreItem>? = null
)