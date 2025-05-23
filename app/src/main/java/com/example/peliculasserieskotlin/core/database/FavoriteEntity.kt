package com.example.peliculasserieskotlin.core.database

import androidx.room.Entity

/**
 * Entidad que representa un elemento favorito en la base de datos.
 * Usa clave primaria compuesta de ID y tipo de medio.
 */
@Entity(tableName = "favorites", primaryKeys = ["mediaId", "mediaType"])
data class FavoriteEntity(
    val mediaId: Int,      // ID del elemento multimedia
    val mediaType: String, // Tipo de medio (MOVIE o SERIES)
)