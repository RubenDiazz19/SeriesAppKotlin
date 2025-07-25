package com.example.seriesappkotlin.features.shared.repository

import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.dao.FavoriteDao
import com.example.seriesappkotlin.core.database.entity.FavoriteEntity
import com.example.seriesappkotlin.core.database.entity.toDomain
import com.example.seriesappkotlin.core.model.Serie
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
            favoriteDao.insertFavorite(FavoriteEntity(userId, item.id))
        }
    }

    suspend fun removeFavorite(serieId: Int) {
        userRepository.getCurrentUserId()?.let { userId ->
            favoriteDao.deleteFavorite(userId, serieId)
        }
    }

    fun isFavorite(id: Int): Flow<Boolean> {
        return userRepository.getCurrentUserId()?.let { userId ->
            favoriteDao.isFavorite(userId, id)
        } ?: flowOf(false)
    }

    fun getFavoriteSeries(): Flow<List<Serie>> {
        return userRepository.getCurrentUserId()?.let { userId ->
            favoriteDao.getAllFavoritesByUser(userId).map { favorites ->
                val ids = favorites.map { it.serieId }
                if (ids.isEmpty()) return@map emptyList()
                val series = serieDao.getSeriesByIds(ids).map { it.toDomain() }
                // Preserve the order from favorites
                ids.mapNotNull { id -> series.find { it.id == id } }
            }
        } ?: flowOf(emptyList())
    }
}