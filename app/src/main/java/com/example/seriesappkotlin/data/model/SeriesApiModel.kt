package com.example.seriesappkotlin.data.model

import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.Serie
import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa una serie desde la API.
 * Mapea la respuesta JSON de series.
 */
data class SeriesApiModel(
    @SerializedName("id")
    val id: Int?,                // ID único de la serie
    
    @SerializedName("name")
    val name: String?,           // Nombre de la serie
    
    @SerializedName("first_air_date")
    val firstAirDate: String?,   // Fecha de primera emisión
    
    @SerializedName("overview")
    val overview: String?,       // Descripción de la serie
    
    @SerializedName("poster_path")
    val posterPath: String?,     // Ruta al póster
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,   // Ruta a la imagen de fondo
    
    @SerializedName("vote_average")
    val voteAverage: Double?,    // Puntuación (0-10)
    
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null // IDs de géneros
) {
    /**
     * Valida que los campos críticos no sean nulos.
     * @return true si los campos críticos son válidos
     */
    fun isValid(): Boolean {
        return id != null && name != null
    }

    /**
     * Convierte un SeriesApiModel directamente a Serie del dominio.
     */
    fun toSerie(): Serie {
        require(isValid()) { "SeriesApiModel no es válido: id o name son nulos" }
        
        return Serie(
            id = id ?: 0,
            title = name ?: "Serie desconocida",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            genres = genreIds?.mapNotNull { id -> GenreItem(id, genreIdToName(id)) },
            originalTitle = name,
            firstAirDate = firstAirDate
        )
    }

    /**
     * Convierte un ID de género a su nombre correspondiente
     */
    private fun genreIdToName(genreId: Int): String {
        return when (genreId) {
            28 -> "Acción"
            12 -> "Aventura"
            16 -> "Animación"
            35 -> "Comedia"
            80 -> "Crimen"
            18 -> "Drama"
            10751 -> "Familiar"
            14 -> "Fantasía"
            36 -> "Historia"
            27 -> "Terror"
            10402 -> "Música"
            9648 -> "Misterio"
            10749 -> "Romance"
            878 -> "Ciencia ficción"
            10770 -> "Película de TV"
            53 -> "Thriller"
            10752 -> "Guerra"
            37 -> "Western"
            else -> "Desconocido"
        }
    }
}

/**
 * Respuesta paginada de la API de series.
 */
data class SeriesApiResponse(
    @SerializedName("results")
    val results: List<SeriesApiModel>  // Lista de series
)
