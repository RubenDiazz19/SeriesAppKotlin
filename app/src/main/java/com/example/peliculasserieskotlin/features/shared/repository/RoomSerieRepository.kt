package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.dao.FavoriteDao
import com.example.peliculasserieskotlin.core.database.dao.SerieDao
import com.example.peliculasserieskotlin.core.database.entity.toDomain
import com.example.peliculasserieskotlin.core.database.entity.toEntity
import com.example.peliculasserieskotlin.core.database.entity.SerieDetailEntity
import com.example.peliculasserieskotlin.core.model.Serie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class RoomSerieRepository @Inject constructor(
    private val serieDao: SerieDao,
    private val favoriteDao: FavoriteDao,
    private val userRepository: UserRepository
) {
    // -------- Serie (cache) --------

    suspend fun cacheSeries(items: List<Serie>) {
        Log.d("RoomSerieRepository", "cacheSeries: items=$items")
        val entities = items.map { it.toEntity() }
        serieDao.insertSeries(entities)
    }

    fun getCachedSeries(): Flow<List<Serie>> {
        Log.d("RoomSerieRepository", "getCachedSeries called")
        return serieDao
            .getAllSeries()
            .map { entityList ->
                Log.d("RoomSerieRepository", "getCachedSeries: entityList=$entityList")
                entityList.map { it.toDomain() }
            }
    }

    suspend fun searchCachedSeries(query: String): List<Serie> {
        Log.d("RoomSerieRepository", "searchCachedSeries: query=$query")
        return serieDao
            .searchSeries(query)
            .map { it.toDomain() }
    }

    fun getAllCachedSeries(): Flow<List<Serie>> {
        Log.d("RoomSerieRepository", "getAllCachedSeries called")
        return serieDao
            .getAllSeries()
            .map { entityList ->
                Log.d("RoomSerieRepository", "getAllCachedSeries: entityList=$entityList")
                entityList.map { it.toDomain() }
            }
    }

    suspend fun getSerieDetail(id: Int): SerieDetailEntity? {
        Log.d("RoomSerieRepository", "getSerieDetail: id=$id")
        return serieDao.getSerieDetailById(id)
    }

    suspend fun cacheSerieDetail(serie: Serie) {
        Log.d("RoomSerieRepository", "cacheSerieDetail: serie=$serie")
        val gson = com.google.gson.Gson()
        val detailEntity = SerieDetailEntity(
            id = serie.id,
            title = serie.title,
            overview = serie.overview,
            posterUrl = serie.posterUrl,
            backdropUrl = serie.backdropUrl,
            voteAverage = serie.voteAverage,
            originalTitle = serie.originalTitle,
            firstAirDate = serie.firstAirDate,
            voteCount = serie.voteCount,
            episodeRunTime = null, // Adjust as per your model
            numberOfSeasons = serie.numberOfSeasons,
            numberOfEpisodes = serie.numberOfEpisodes,
            genres = gson.toJson(serie.genres),
            status = serie.status,
            tagline = serie.tagline,
            seasons = gson.toJson(serie.seasons),
            popularity = null, // Adjust as per your model
            productionCompanies = null, // Adjust as per your model
            productionCountries = null, // Adjust as per your model
            spokenLanguages = null, // Adjust as per your model
            networks = null // Adjust as per your model
        )
        Log.d("RoomSerieRepository", "cacheSerieDetail: detailEntity=$detailEntity")
        serieDao.insertSerieDetail(detailEntity)
    }

    suspend fun clearExpiredCache() {
        val now = System.currentTimeMillis()
        val expiration = 24 * 60 * 60 * 1000 // 24 horas

        val expiredItems = serieDao.getAllSeriesList().filter { now - it.timestamp > expiration }
        expiredItems.forEach { serieDao.deleteById(it.id) }
    }

    suspend fun clearCacheExceptFavorites() {
        val favorites = favoriteDao.getAllFavorites().first()
        val favoriteSet = favorites.map { it.mediaId }.toSet()

        val allItems = serieDao.getAllSeriesList()
        for (item in allItems) {
            if (item.id !in favoriteSet) { 
                serieDao.deleteById(item.id)
            }
        }
    }
}
