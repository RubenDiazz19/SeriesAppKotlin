package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Modelo que representa una película desde la API.
 * Mapea la respuesta JSON de películas.
 */
data class MovieApiModel(
    @SerializedName("id")
    val id: Int?,                // ID único de la película
    
    @SerializedName("title")
    val title: String?,          // Título de la película
    
    @SerializedName("release_date")
    val releaseDate: String?,    // Fecha de lanzamiento
    
    @SerializedName("overview")
    val overview: String?,       // Descripción de la película
    
    @SerializedName("poster_path")
    val posterPath: String?,     // Ruta al póster
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,   // Ruta a la imagen de fondo
    
    @SerializedName("vote_average")
    val voteAverage: Double?     // Puntuación (0-10)
) {
    /**
     * Valida que los campos críticos no sean nulos.
     * @return true si los campos críticos son válidos
     */
    fun isValid(): Boolean {
        return id != null && title != null
    }

    /**
     * Convierte un MovieApiModel a un MediaItem del dominio.
     * Maneja valores nulos con valores predeterminados.
     */
    fun toDomain(): MediaItem {
        require(isValid()) { "MovieApiModel no es válido: id o title son nulos" }
        
        return MediaItem(
            id = id ?: 0,
            title = title ?: "Título desconocido",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            type = MediaType.MOVIE
        )
    }
}

/**
 * Respuesta paginada de la API de películas.
 */
data class MovieApiResponse(
    @SerializedName("results")
    val results: List<MovieApiModel>  // Lista de películas
)

// Función de extensión para compatibilidad
fun MovieApiModel.toDomain(): MediaItem = this.toDomain()
