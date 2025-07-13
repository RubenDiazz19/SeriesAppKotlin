package com.example.seriesappkotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.seriesappkotlin.core.database.dao.FavoriteDao
import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.dao.UserDao
import com.example.seriesappkotlin.core.database.dao.WatchedDao
import com.example.seriesappkotlin.core.database.entity.FavoriteEntity
import com.example.seriesappkotlin.core.database.entity.SerieDetailEntity
import com.example.seriesappkotlin.core.database.entity.SerieEntity
import com.example.seriesappkotlin.core.database.entity.SeasonEntity
import com.example.seriesappkotlin.core.database.entity.UserEntity
import com.example.seriesappkotlin.core.database.entity.WatchedEntity

/**
 * Base de datos Room de la aplicación.
 * Contiene las tablas de elementos multimedia, favoritos y detalles.
 */
@Database(
    entities = [
        SerieEntity::class,
        WatchedEntity::class,
        SerieDetailEntity::class,
        UserEntity::class,
        SeasonEntity::class,
        FavoriteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serieDao(): SerieDao
    abstract fun watchedDao(): WatchedDao
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
}