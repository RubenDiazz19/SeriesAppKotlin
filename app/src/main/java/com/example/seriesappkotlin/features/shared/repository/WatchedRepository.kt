package com.example.seriesappkotlin.features.shared.repository

import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.dao.WatchedDao
import com.example.seriesappkotlin.core.database.entity.WatchedEntity
import com.example.seriesappkotlin.core.database.entity.toDomain
import com.example.seriesappkotlin.core.model.Serie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchedRepository @Inject constructor(
    private val watchedDao: WatchedDao,
    private val serieDao: SerieDao,
    private val userRepository: UserRepository
) {

    suspend fun addWatched(item: Serie) {
        userRepository.getCurrentUserId()?.let { userId ->
            watchedDao.insertWatched(WatchedEntity(userId, item.id))
        }
    }

    suspend fun removeWatched(serieId: Int) {
        userRepository.getCurrentUserId()?.let {userId ->
            watchedDao.deleteWatched(userId, serieId)
        }
    }

    fun isWatched(id: Int): Flow<Boolean> {
        return userRepository.getCurrentUserId()?.let {userId ->
            watchedDao.isWatched(userId, id)
        } ?: flowOf(false)
    }

    fun getWatchedSeries(): Flow<List<Serie>> {
        return userRepository.getCurrentUserId()?.let {userId ->
            watchedDao.getAllWatchedByUser(userId).map { watched ->
                val ids = watched.map { it.serieId }
                if (ids.isEmpty()) return@map emptyList()
                val series = serieDao.getSeriesByIds(ids).map { it.toDomain() }
                // Preserve the order from watched
                ids.mapNotNull { id -> series.find { it.id == id } }
            }
        } ?: flowOf(emptyList())
    }
}