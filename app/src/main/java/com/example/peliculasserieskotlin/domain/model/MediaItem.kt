package com.example.peliculasserieskotlin.domain.model

enum class MediaType {
    MOVIE, SERIES // Cambio de TV a SERIES
}

data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val type: MediaType
)
