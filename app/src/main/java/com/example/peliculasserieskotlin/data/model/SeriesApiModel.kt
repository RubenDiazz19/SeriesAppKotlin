package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
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
     * Convierte un SeriesApiModel a un MediaItem del dominio.
     * Unifica series y películas bajo un mismo modelo.
     */
    fun toDomain(): MediaItem {
        require(isValid()) { "SeriesApiModel no es válido: id o name son nulos" }
        
        return MediaItem(
            id = id ?: 0,
            title = name ?: "Serie desconocida",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            type = MediaType.SERIES,
            genres = genreIds?.mapNotNull { id -> GenreItem(id, genreIdToName(id)) }
        )
    }
}

/**
 * Respuesta paginada de la API de series.
 */
data class SeriesApiResponse(
    @SerializedName("results")
    val results: List<SeriesApiModel>  // Lista de series
)

// Función de extensión para compatibilidad
fun SeriesApiModel.toDomain(): MediaItem = this.toDomain()
