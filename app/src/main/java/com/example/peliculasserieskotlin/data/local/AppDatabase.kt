package com.example.peliculasserieskotlin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.peliculasserieskotlin.data.local.MediaItemEntity
import com.example.peliculasserieskotlin.domain.model.MediaItem

/**
 * Base de datos Room de la aplicación.
 * Contiene las tablas de elementos multimedia y favoritos.
 */
@Database(entities = [MediaItemEntity::class, FavoriteEntity::class], version = 1, exportSchema = false) // Añadido FavoriteEntity
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun favoriteDao(): FavoriteDao // Añadido FavoriteDao
}

