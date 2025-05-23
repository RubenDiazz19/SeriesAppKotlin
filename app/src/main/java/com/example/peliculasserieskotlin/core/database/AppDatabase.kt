package com.example.peliculasserieskotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.peliculasserieskotlin.core.database.FavoriteEntity

/**
 * Base de datos Room de la aplicación.
 * Contiene las tablas de elementos multimedia y favoritos.
 */
@Database(entities = [MediaItemEntity::class, FavoriteEntity::class], version = 1, exportSchema = false) // Añadido FavoriteEntity
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun favoriteDao(): FavoriteDao // Añadido FavoriteDao
}