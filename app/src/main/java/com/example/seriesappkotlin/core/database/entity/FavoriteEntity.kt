package com.example.seriesappkotlin.core.database.entity

import androidx.room.Entity

/**
 * Entidad que representa una serie favorita en la base de datos.
 */
@Entity(tableName = "favorite_series", primaryKeys = ["userId", "serieId"])
data class FavoriteEntity(
    val userId: Int,      // ID del usuario que marcó como favorita
    val serieId: Int,     // ID de la serie
)