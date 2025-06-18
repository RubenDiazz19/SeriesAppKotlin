package com.example.peliculasserieskotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.MediaDetailDao
import com.example.peliculasserieskotlin.core.database.dao.MediaItemDao
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.entity.MediaDetailEntity
import com.example.peliculasserieskotlin.core.database.entity.MediaItemEntity

/**
 * Base de datos Room de la aplicación.
 * Contiene las tablas de elementos multimedia, favoritos y detalles.
 */
@Database(
    entities = [
        MediaItemEntity::class,
        FavoriteEntity::class,
        MediaDetailEntity::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun mediaDetailDao(): MediaDetailDao
}