package com.example.peliculasserieskotlin.core.model

data class Serie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val genres: List<GenreItem>? = null,
    val seasons: List<Season> = emptyList(),
    val originalTitle: String? = null,
    val firstAirDate: String? = null,
    val voteCount: Int? = null,
    val runtime: Int? = null,
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val status: String? = null,
    val tagline: String? = null
)