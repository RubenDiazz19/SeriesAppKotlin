package com.example.peliculasserieskotlin.core.model

/**
 * Tipos de contenido multimedia.
 */
enum class MediaType {
    MOVIE, SERIES // Cambio de TV a SERIES
}

/**
 * Modelo que representa un elemento multimedia (película o serie).
 */
open class MediaItem(
    open val id: Int,               // ID único
    open val title: String,         // Título
    open val overview: String,      // Descripción
    open val posterUrl: String,     // URL del póster
    open val backdropUrl: String?,  // URL de imagen de fondo
    open val voteAverage: Double,   // Puntuación (0-10)
    open val type: MediaType,       // Tipo (MOVIE o SERIES)
    open val genres: List<GenreItem>? = null, // Lista de géneros opcional
    open val timestamp: Long? = null // Marca de tiempo opcional para caché
)
