package com.example.peliculasserieskotlin.data.api.model

import com.example.peliculasserieskotlin.domain.model.Movie
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MovieApiModel(
    val id: Int?,
    val title: String?,
    val release_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?
)

fun MovieApiModel.toDomain(): Movie {
    // Convertir release_date a año
    val year = release_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    // Construir URL completa de la imagen
    val posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""

    return Movie(
        id = this.id ?: 0,
        title = this.title ?: "Título desconocido",
        year = year,
        overview = this.overview ?: "Sin descripción",
        posterUrl = posterUrl,
        voteAverage = this.vote_average ?: 0.0
    )
}

data class MovieApiResponse(
    val results: List<MovieApiModel>
)
