package com.example.peliculasserieskotlin.data.api.model

import com.example.peliculasserieskotlin.domain.model.Movie

data class MovieApiModel(
    val id: Int?,
    val title: String?,
    val release_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?
)

fun MovieApiModel.toDomain(): Movie {
    val year = release_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    val posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""

    return Movie(
        id = id ?: 0,
        title = title ?: "Título desconocido",
        year = year,
        overview = overview ?: "Sin descripción",
        posterUrl = posterUrl,
        voteAverage = vote_average ?: 0.0
    )
}

data class MovieApiResponse(
    val results: List<MovieApiModel>
)
