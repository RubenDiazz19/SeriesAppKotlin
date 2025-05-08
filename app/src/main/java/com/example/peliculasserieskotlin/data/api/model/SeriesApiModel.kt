package com.example.peliculasserieskotlin.data.api.model

import com.example.peliculasserieskotlin.domain.model.MediaItem // Cambio aquí
import com.example.peliculasserieskotlin.domain.model.MediaType // Nuevo import

data class SeriesApiModel(
    val id: Int?,
    val name: String?, // Las series usan 'name'
    val first_air_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?
)

fun SeriesApiModel.toDomain(): MediaItem { // Cambio el tipo de retorno a MediaItem

    return MediaItem( // Cambio aquí para retornar MediaItem
        id = id ?: 0,
        title = name ?: "Serie desconocida", // Mapeamos 'name' a 'title'
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        voteAverage = vote_average ?: 0.0,
        type = MediaType.SERIES,
        backdropUrl = first_air_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
    )
}

data class SeriesApiResponse(
    val results: List<SeriesApiModel>
)
