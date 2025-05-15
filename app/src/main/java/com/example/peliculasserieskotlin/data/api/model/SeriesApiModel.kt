package com.example.peliculasserieskotlin.data.api.model

import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType

/**
 * Modelo que representa una serie desde la API.
 * Mapea la respuesta JSON de series.
 */
data class SeriesApiModel(
    val id: Int?,                // ID único de la serie
    val name: String?,           // Nombre de la serie
    val first_air_date: String?, // Fecha de primera emisión
    val overview: String?,       // Descripción de la serie
    val poster_path: String?,    // Ruta al póster
    val vote_average: Double?    // Puntuación (0-10)
)

/**
 * Convierte un SeriesApiModel a un MediaItem del dominio.
 * Unifica series y películas bajo un mismo modelo.
 */
fun SeriesApiModel.toDomain(): MediaItem {
    return MediaItem(
        id = id ?: 0,
        title = name ?: "Serie desconocida",
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        voteAverage = vote_average ?: 0.0,
        type = MediaType.SERIES,
        backdropUrl = first_air_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    )
}

/**
 * Respuesta paginada de la API de series.
 */
data class SeriesApiResponse(
    val results: List<SeriesApiModel>  // Lista de series
)
