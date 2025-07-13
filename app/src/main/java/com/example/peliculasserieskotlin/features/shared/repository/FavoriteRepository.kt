package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.SerieDao
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.entity.toDomain
import com.example.peliculasserieskotlin.core.database.entity.toEntity
import com.example.peliculasserieskotlin.core.model.Serie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val serieDao: SerieDao,
    private val userRepository: UserRepository
) {

    suspend fun addFavorite(item: Serie) {
        userRepository.getCurrentUserId()?.let { userId ->
            favoriteDao.insertFavorite(FavoriteEntity(userId, item.id, "SERIES"))
        }
    }

    suspend fun removeFavorite(serieId: Int) {
        userRepository.getCurrentUserId()?.let {userId ->
            favoriteDao.deleteFavorite(userId, serieId)
        }
    }

    fun isFavorite(id: Int): Flow<Boolean> {
        return userRepository.getCurrentUserId()?.let {userId ->
            favoriteDao.isFavorite(userId, id)
        } ?: flowOf(false)
    }

    fun getFavoriteSeries(): Flow<List<Serie>> {
        return userRepository.getCurrentUserId()?.let {userId ->
            favoriteDao.getAllFavoritesByUser(userId).map { favs ->
                val ids = favs.map { it.mediaId }
                if (ids.isEmpty()) return@map emptyList()
                val series = serieDao.getSeriesByIds(ids).map { it.toDomain() }
                // Preserve the order from favorites
                ids.mapNotNull { id -> series.find { it.id == id } }
            }
        } ?: flowOf(emptyList())
    }
}