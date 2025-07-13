package com.example.seriesappkotlin.core.database.entity

import androidx.room.Entity

/**
 * Entidad que representa una serie vista en la base de datos.
 */
@Entity(tableName = "watched_series", primaryKeys = ["userId", "serieId"])
data class WatchedEntity(
    val userId: Int,      // ID del usuario que marcó como vista
    val serieId: Int,     // ID de la serie
)