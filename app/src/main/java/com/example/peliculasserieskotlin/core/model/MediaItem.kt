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
data class MediaItem(
    val id: Int,               // ID único
    val title: String,         // Título
    val overview: String,      // Descripción
    val posterUrl: String,     // URL del póster
    val backdropUrl: String?,  // URL de imagen de fondo
    val voteAverage: Double,   // Puntuación (0-10)
    val type: MediaType        // Tipo (MOVIE o SERIES)
)
