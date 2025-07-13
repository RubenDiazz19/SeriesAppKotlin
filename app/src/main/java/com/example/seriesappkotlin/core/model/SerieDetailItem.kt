package com.example.seriesappkotlin.core.model

object SerieDetailItem {
    data class SeriesDetailItem(
        val id: Int,
        val title: String,
        val overview: String,
        val posterUrl: String,
        val backdropUrl: String?,
        val voteAverage: Double,
        val originalTitle: String?,
        val firstAirDate: String?,
        val voteCount: Int?,
        val runtime: Int?,
        val numberOfSeasons: Int?,
        val numberOfEpisodes: Int?,
        val genres: List<GenreItem>?,
        val status: String?,
        val tagline: String?
    )
}