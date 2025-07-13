package com.example.seriesappkotlin.features.shared.repository

import com.example.seriesappkotlin.core.database.dao.SerieDao
import com.example.seriesappkotlin.core.database.entity.toDomain
import com.example.seriesappkotlin.core.database.entity.toEntity
import com.example.seriesappkotlin.core.database.entity.SerieDetailEntity
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.model.Season
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class RoomSerieRepository @Inject constructor(
    private val serieDao: SerieDao
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
    
    // -------- Season Details (cache) --------
    
    suspend fun getSeasonDetails(serieId: Int, seasonNumber: Int): Season? {
        Log.d("RoomSerieRepository", "getSeasonDetails: serieId=$serieId, seasonNumber=$seasonNumber")
        return serieDao.getSeasonDetails(serieId, seasonNumber)?.toDomain()
    }
    
    suspend fun cacheSeasonDetails(season: Season, serieId: Int) {
        Log.d("RoomSerieRepository", "cacheSeasonDetails: season=$season, serieId=$serieId")
        val seasonEntity = season.toEntity(serieId)
        serieDao.insertSeason(seasonEntity)
    }
    
    suspend fun deleteSeasonDetails(serieId: Int, seasonNumber: Int) {
        Log.d("RoomSerieRepository", "deleteSeasonDetails: serieId=$serieId, seasonNumber=$seasonNumber")
        serieDao.deleteSeasonDetails(serieId, seasonNumber)
    }
    
    suspend fun getAllSeasonsForSerie(serieId: Int): List<Season> {
        Log.d("RoomSerieRepository", "getAllSeasonsForSerie: serieId=$serieId")
        return serieDao.getAllSeasonsForSerie(serieId).map { it.toDomain() }
    }
}
