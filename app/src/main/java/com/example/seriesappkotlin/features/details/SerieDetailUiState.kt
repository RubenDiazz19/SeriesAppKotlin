package com.example.seriesappkotlin.features.details

import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.Season

// Clase de estado para la UI, conteniendo datos listos para mostrar
data class SerieDetailUiState(
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
    val genres: List<GenreItem>? = null,
    val numberOfSeasons: Int? = null,      // Para series
    val numberOfEpisodes: Int? = null,     // Para series
    val seasons: List<Season>? = null,
    val error: String? = null
)