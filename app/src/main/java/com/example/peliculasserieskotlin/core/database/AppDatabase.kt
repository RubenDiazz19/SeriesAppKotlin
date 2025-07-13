package com.example.peliculasserieskotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.SerieDao
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.entity.SerieEntity
import com.example.peliculasserieskotlin.core.database.entity.SerieDetailEntity
import com.example.peliculasserieskotlin.core.database.entity.UserEntity
import com.example.peliculasserieskotlin.core.database.dao.UserDao

/**
 * Base de datos Room de la aplicación.
 * Contiene las tablas de elementos multimedia, favoritos y detalles.
 */
@Database(
    entities = [
        SerieEntity::class,
        FavoriteEntity::class,
        SerieDetailEntity::class,
        UserEntity::class
    ], 
    version = 7, // Incrementa la versión de la base de datos
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serieDao(): SerieDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userDao(): UserDao
}