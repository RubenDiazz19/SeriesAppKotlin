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
    // Convertir first_air_date a año
    val year = first_air_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    // Construir URL completa de la imagen
    val posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""

    return Series(
        id = this.id ?: 0,
        name = this.name ?: "Serie desconocida",
        year = year,
        overview = this.overview ?: "Sin descripción",
        posterUrl = posterUrl,
        voteAverage = this.vote_average ?: 0.0
    )
}

data class SeriesApiResponse(
    val results: List<SeriesApiModel>
)
