package com.example.seriesappkotlin.features.shared.repository

import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.dao.WatchedDao
import com.example.seriesappkotlin.core.database.dao.WatchedSeasonDao
import com.example.seriesappkotlin.core.database.dao.WatchedEpisodeDao
import com.example.seriesappkotlin.core.database.entity.WatchedEntity
import com.example.seriesappkotlin.core.database.entity.WatchedSeasonEntity
import com.example.seriesappkotlin.core.database.entity.WatchedEpisodeEntity
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
    private val watchedSeasonDao: WatchedSeasonDao,
    private val watchedEpisodeDao: WatchedEpisodeDao, // Agregar inyección
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
    
    // Métodos para temporadas vistas
    suspend fun addWatchedSeason(serieId: Int, seasonNumber: Int) {
        userRepository.getCurrentUserId()?.let { userId ->
            watchedSeasonDao.insertWatchedSeason(WatchedSeasonEntity(userId, serieId, seasonNumber))
        }
    }

    suspend fun removeWatchedSeason(serieId: Int, seasonNumber: Int) {
        userRepository.getCurrentUserId()?.let { userId ->
            watchedSeasonDao.deleteWatchedSeason(userId, serieId, seasonNumber)
        }
    }

    fun isSeasonWatched(serieId: Int, seasonNumber: Int): Flow<Boolean> {
        return userRepository.getCurrentUserId()?.let { userId ->
            watchedSeasonDao.isSeasonWatched(userId, serieId, seasonNumber)
        } ?: flowOf(false)
    }
    
    // Métodos para episodios vistos
    suspend fun addWatchedEpisode(serieId: Int, seasonNumber: Int, episodeNumber: Int) {
        userRepository.getCurrentUserId()?.let { userId ->
            watchedEpisodeDao.insertWatchedEpisode(WatchedEpisodeEntity(userId, serieId, seasonNumber, episodeNumber))
        }
    }

    suspend fun removeWatchedEpisode(serieId: Int, seasonNumber: Int, episodeNumber: Int) {
        userRepository.getCurrentUserId()?.let { userId ->
            watchedEpisodeDao.deleteWatchedEpisode(userId, serieId, seasonNumber, episodeNumber)
        }
    }

    fun isEpisodeWatched(serieId: Int, seasonNumber: Int, episodeNumber: Int): Flow<Boolean> {
        return userRepository.getCurrentUserId()?.let { userId ->
            watchedEpisodeDao.isEpisodeWatched(userId, serieId, seasonNumber, episodeNumber)
        } ?: flowOf(false)
    }

    fun getWatchedEpisodesForSeason(serieId: Int, seasonNumber: Int): Flow<List<WatchedEpisodeEntity>> {
        return userRepository.getCurrentUserId()?.let { userId ->
            watchedEpisodeDao.getWatchedEpisodesForSerie(userId, serieId).map { episodes ->
                episodes.filter { it.seasonNumber == seasonNumber }
            }
        } ?: flowOf(emptyList())
    }
}