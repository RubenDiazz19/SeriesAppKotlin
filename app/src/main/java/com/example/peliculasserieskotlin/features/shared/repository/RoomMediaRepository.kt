package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.FavoriteDao
import com.example.peliculasserieskotlin.core.database.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.MediaItemDao
import com.example.peliculasserieskotlin.core.database.toDomain
import com.example.peliculasserieskotlin.core.database.toEntity
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMediaRepository(
    private val mediaItemDao: MediaItemDao,
    private val favoriteDao: FavoriteDao
) {
    // -------- MediaItem (cache) --------

    suspend fun cacheMediaItems(items: List<MediaItem>) {
        val entities = items.map { it.toEntity() }
        mediaItemDao.insertMediaItems(entities)
    }

    fun getCachedMediaItems(type: MediaType): Flow<List<MediaItem>> {
        return mediaItemDao
            .getAllMedia(type.name)
            .map { entityList -> entityList.map { it.toDomain() } }
    }

    suspend fun searchCachedMediaItems(type: MediaType, query: String): List<MediaItem> {
        return mediaItemDao
            .searchMedia(query, type.name)
            .map { it.toDomain() }
    }

    // -------- Favorites --------

    suspend fun addFavorite(item: MediaItem) {
        // Asegúrate de que el MediaItem esté en la base de datos antes de marcarlo como favorito
        mediaItemDao.insertMediaItem(item.toEntity())
        favoriteDao.insertFavorite(FavoriteEntity(item.id, item.type.name))
    }

    suspend fun removeFavorite(id: Int, type: MediaType) {
        favoriteDao.deleteFavorite(FavoriteEntity(id, type.name))
    }

    fun getAllFavorites(type: MediaType): Flow<List<MediaItem>> {
        return favoriteDao.getFavoritesByType(type.name).map { favs ->
            val ids = favs.map { it.mediaId }
            if (ids.isEmpty()) return@map emptyList()
            // Consulta optimizada usando IN
            mediaItemDao.getMediaItemsByIds(ids)
                .map { it.toDomain() }
                .filter { it.type == type }
                .sortedBy { ids.indexOf(it.id) }
        }
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> {
        return favoriteDao.isFavorite(id, type.name)
    }
}
