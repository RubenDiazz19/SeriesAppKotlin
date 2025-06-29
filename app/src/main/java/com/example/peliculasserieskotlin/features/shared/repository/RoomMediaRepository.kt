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
        // Implementar lógica para limpiar caché expirado si es necesario
        // Por ahora, no hacemos nada
    }

    suspend fun hasDetailCached(id: Int, type: MediaType): Boolean {
        return mediaDetailDao.existsDetail(id, type.name) > 0
    }

    // -------- Favorites --------

    suspend fun addFavorite(item: MediaItem) {
        val currentUserId = userRepository.getCurrentUserId()
        if (currentUserId != null) {
            // Asegúrate de que el MediaItem esté en la base de datos antes de marcarlo como favorito
            mediaItemDao.insertMediaItem(item.toEntity())
            favoriteDao.insertFavorite(FavoriteEntity(currentUserId, item.id, item.type.name))
        }
    }

    suspend fun removeFavorite(id: Int, type: MediaType) {
        val currentUserId = userRepository.getCurrentUserId()
        if (currentUserId != null) {
            favoriteDao.deleteFavorite(FavoriteEntity(currentUserId, id, type.name))
        }
    }

    fun getAllFavorites(type: MediaType): Flow<List<MediaItem>> {
        val currentUserId = userRepository.getCurrentUserId()
        return if (currentUserId != null) {
            favoriteDao.getFavoritesByTypeAndUser(currentUserId, type.name).map { favs ->
                val ids = favs.map { it.mediaId }
                if (ids.isEmpty()) return@map emptyList()
                // Consulta optimizada usando IN
                mediaItemDao.getMediaItemsByIds(ids)
                    .map { it.toDomain() }
                    .filter { it.type == type }
                    .sortedBy { ids.indexOf(it.id) }
            }
        } else {
            // Para invitados, devolver lista vacía
            flowOf(emptyList())
        }
    }

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> {
        val currentUserId = userRepository.getCurrentUserId()
        return if (currentUserId != null) {
            favoriteDao.isFavorite(currentUserId, id, type.name)
        } else {
            // Para invitados, siempre devolver false
            flowOf(false)
        }
    }
}
