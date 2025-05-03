package com.example.peliculasserieskotlin.data.api.model

import com.example.peliculasserieskotlin.domain.model.Series

data class SeriesApiModel(
    val id: Int?,
    val name: String?,
    val first_air_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?
)

fun SeriesApiModel.toDomain(): Series {
    val year = first_air_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    val posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""

    return Series(
        id = id ?: 0,
        name = name ?: "Serie desconocida",
        year = year,
        overview = overview ?: "Sin descripción",
        posterUrl = posterUrl,
        voteAverage = vote_average ?: 0.0
    )
}

data class SeriesApiResponse(
    val results: List<SeriesApiModel>
)
