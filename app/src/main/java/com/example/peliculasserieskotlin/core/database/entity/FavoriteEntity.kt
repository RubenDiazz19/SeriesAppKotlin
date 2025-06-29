package com.example.peliculasserieskotlin.core.database.entity

import androidx.room.Entity

/**
 * Entidad que representa un elemento favorito en la base de datos.
 * Usa clave primaria compuesta de userId, mediaId y tipo de medio.
 */
@Entity(tableName = "favorites", primaryKeys = ["userId", "mediaId", "mediaType"])
data class FavoriteEntity(
    val userId: Int,      // ID del usuario que marcó como favorito
    val mediaId: Int,     // ID del elemento multimedia
    val mediaType: String // Tipo de medio (MOVIE o SERIES)
)