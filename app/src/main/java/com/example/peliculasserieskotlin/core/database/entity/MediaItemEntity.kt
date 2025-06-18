package com.example.peliculasserieskotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType

/**
 * Entidad que representa un elemento multimedia en la base de datos.
 * Incluye índices para optimizar búsquedas por título y valoración.
 */
@Entity(
    tableName = "media_items",
    indices = [Index(value = ["title"]), Index(value = ["voteAverage"])]
)
data class MediaItemEntity(
    @PrimaryKey
    val id: Int,               // ID único del elemento
    val title: String,         // Título
    val overview: String,      // Descripción
    val posterUrl: String,     // URL del póster
    val backdropUrl: String?,  // URL de imagen de fondo
    val voteAverage: Double,   // Valoración media
    val mediaType: String      // Tipo: "MOVIE" o "SERIES"
)

/**
 * Convierte una entidad a objeto de dominio.
 */
fun MediaItemEntity.toDomain(): MediaItem {
    return MediaItem(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        type = MediaType.valueOf(mediaType)
    )
}

/**
 * Convierte un objeto de dominio a entidad.
 */
fun MediaItem.toEntity(): MediaItemEntity {
    return MediaItemEntity(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        mediaType = type.name
    )
}