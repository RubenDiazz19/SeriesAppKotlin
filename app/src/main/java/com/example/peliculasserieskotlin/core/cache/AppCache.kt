package com.example.peliculasserieskotlin.core.cache

import android.content.Context
import androidx.room.Room
import com.example.peliculasserieskotlin.core.database.AppDatabase
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.entity.toDomain
import com.example.peliculasserieskotlin.core.database.entity.toEntity
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCache @Inject constructor(
    private val context: Context
) {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "media_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // Cachear lista de películas
    suspend fun cacheMovies(movies: List<MediaItem>) {
        database.mediaItemDao().insertMediaItems(movies.map { it.toEntity() })
    }

    fun getCachedMovies(): Flow<List<MediaItem>> {
        return database.mediaItemDao().getAllMedia(MediaType.MOVIE.name).map { list -> list.map { it.toDomain() } }
    }

    // Cachear lista de series
    suspend fun cacheSeries(series: List<MediaItem>) {
        database.mediaItemDao().insertMediaItems(series.map { it.toEntity() })
    }

    fun getCachedSeries(): Flow<List<MediaItem>> {
        return database.mediaItemDao().getAllMedia(MediaType.SERIES.name).map { list -> list.map { it.toDomain() } }
    }

    // Obtener un MediaItem por id y tipo
    suspend fun getCachedMediaItem(id: Int, type: MediaType): MediaItem? {
        return database.mediaItemDao().getMediaById(id, type.name)?.toDomain()
    }

    // Favoritos
    suspend fun addToFavorites(mediaItem: MediaItem) {
        database.mediaItemDao().insertMediaItem(mediaItem.toEntity())
        database.favoriteDao().insertFavorite(FavoriteEntity(mediaItem.id, mediaItem.type.name))
    }

    suspend fun removeFromFavorites(mediaId: Int, mediaType: MediaType) {
        database.favoriteDao().deleteFavorite(FavoriteEntity(mediaId, mediaType.name))
    }

    fun getFavorites(): Flow<List<MediaItem>> {
        return database.favoriteDao().getAllFavorites().map { favs ->
            val ids = favs.map { it.mediaId }
            if (ids.isEmpty()) return@map emptyList()
            database.mediaItemDao().getMediaItemsByIds(ids).map { it.toDomain() }
        }
    }

    /**
     * Borra la caché de media_items y media_details excepto los favoritos.
     */
    suspend fun clearCacheExceptFavorites() {
        // Obtener favoritos
        val favorites = database.favoriteDao().getAllFavorites().first()
        val favoriteSet = favorites.map { it.mediaId to it.mediaType }.toSet()
        // Limpiar media_items
        val allItems = database.mediaItemDao().getAllMediaItemsList()
        for (item in allItems) {
            if ((item.id to item.mediaType) !in favoriteSet) {
                database.mediaItemDao().deleteByIdAndType(item.id, item.mediaType)
            }
        }
        // Limpiar media_details
        val allDetails = database.mediaDetailDao().getAllDetailsList()
        for (detail in allDetails) {
            if ((detail.id to detail.mediaType) !in favoriteSet) {
                database.mediaDetailDao().deleteByIdAndType(detail.id, detail.mediaType)
            }
        }
    }
}