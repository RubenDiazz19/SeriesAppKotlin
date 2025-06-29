package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.dao.MediaItemDao
import com.example.peliculasserieskotlin.core.database.dao.MediaDetailDao
import com.example.peliculasserieskotlin.core.database.entity.MediaDetailEntity
import com.example.peliculasserieskotlin.core.database.entity.toDomain
import com.example.peliculasserieskotlin.core.database.entity.toEntity
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RoomMediaRepository @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val favoriteDao: FavoriteDao,
    private val mediaDetailDao: MediaDetailDao,
    private val userRepository: UserRepository
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

    fun getAllCachedMedia(): Flow<List<MediaItem>> {
        return mediaItemDao
            .getAllMediaItems()
            .map { entityList -> entityList.map { it.toDomain() } }
    }

    suspend fun searchCachedMediaItems(type: MediaType, query: String): List<MediaItem> {
        return mediaItemDao
            .searchMedia(query, type.name)
            .map { it.toDomain() }
    }

    // -------- MediaDetail (cache) --------

    suspend fun cacheDetail(detail: MediaDetailItem) {
        val entity = detail.toEntity()
        mediaDetailDao.insertDetail(entity)
    }

    suspend fun getCachedDetail(id: Int, type: MediaType): MediaDetailEntity? {
        return mediaDetailDao.getDetailById(id, type.name)
    }

    fun getCachedDetailFlow(id: Int, type: MediaType): Flow<MediaDetailEntity?> {
        return mediaDetailDao.getDetailByIdFlow(id, type.name)
    }

    fun getAllCachedDetails(type: MediaType): Flow<List<MediaDetailEntity>> {
        return mediaDetailDao.getAllDetailsByType(type.name)
    }

    suspend fun deleteCachedDetail(id: Int, type: MediaType) {
        mediaDetailDao.deleteDetail(id, type.name)
    }

    suspend fun clearExpiredCache() {
        val now = System.currentTimeMillis()
        val expiration = 24 * 60 * 60 * 1000 // 24 horas

        // Limpiar media_items expirados
        val expiredItems = mediaItemDao.getAllMediaItemsList().filter { now - it.timestamp > expiration }
        expiredItems.forEach { mediaItemDao.deleteByIdAndType(it.id, it.mediaType) }

        // Limpiar media_details expirados
        val expiredDetails = mediaDetailDao.getAllDetailsList().filter { now - it.lastUpdated > expiration }
        expiredDetails.forEach { mediaDetailDao.deleteByIdAndType(it.id, it.mediaType) }
    }

    suspend fun hasDetailCached(id: Int, type: MediaType): Boolean {
        return mediaDetailDao.existsDetail(id, type.name) > 0
    }

    // -------- Favorites --------

    // (Eliminados métodos de favoritos, usar FavoriteRepository)

    /**
     * Borra la caché de media_items y media_details excepto los favoritos.
     */
    suspend fun clearCacheExceptFavorites() {
        // Obtener favoritos (legacy: userId = -1)
        val favorites = favoriteDao.getAllFavorites().first()
        val favoriteSet = favorites.map { it.mediaId to it.mediaType }.toSet()
        // Limpiar media_items
        val allItems = mediaItemDao.getAllMediaItemsList()
        for (item in allItems) {
            if ((item.id to item.mediaType) !in favoriteSet) {
                mediaItemDao.deleteByIdAndType(item.id, item.mediaType)
            }
        }
        // Limpiar media_details
        val allDetails = mediaDetailDao.getAllDetailsList()
        for (detail in allDetails) {
            if ((detail.id to detail.mediaType) !in favoriteSet) {
                mediaDetailDao.deleteByIdAndType(detail.id, detail.mediaType)
            }
        }
    }
}
