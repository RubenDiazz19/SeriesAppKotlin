package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.GenreItem
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
    val voteAverage: Double?,    // Puntuación (0-10)
    
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null // IDs de géneros
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
            type = MediaType.MOVIE,
            genres = genreIds?.mapNotNull { id -> GenreItem(id, genreIdToName(id)) }
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

// Función auxiliar para mapear IDs a nombres de género (ajusta según tus géneros soportados)
fun genreIdToName(id: Int): String = when (id) {
    28 -> "Acción"
    12 -> "Aventura"
    16 -> "Animación"
    35 -> "Comedia"
    80 -> "Crimen"
    99 -> "Documental"
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
    53 -> "Suspense"
    10752 -> "Bélica"
    37 -> "Western"
    else -> "Otro"
}
