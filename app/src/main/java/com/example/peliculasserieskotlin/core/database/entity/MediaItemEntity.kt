package com.example.peliculasserieskotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.google.gson.Gson
import com.example.peliculasserieskotlin.core.model.GenreItem

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
    val mediaType: String,     // Tipo: "MOVIE" o "SERIES"
    val genres: String? = null // Géneros en formato JSON
)

/**
 * Convierte una entidad a objeto de dominio.
 */
fun MediaItemEntity.toDomain(): MediaItem {
    val gson = Gson()
    val genresList = genres?.let {
        val type = object : com.google.gson.reflect.TypeToken<List<GenreItem>>() {}.type
        gson.fromJson<List<GenreItem>>(it, type)
    }
    return MediaItem(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        type = MediaType.valueOf(mediaType),
        genres = genresList
    )
}

/**
 * Convierte un objeto de dominio a entidad.
 */
fun MediaItem.toEntity(): MediaItemEntity {
    val gson = Gson()
    return MediaItemEntity(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        mediaType = type.name,
        genres = genres?.let { gson.toJson(it) }
    )
}