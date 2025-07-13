package com.example.seriesappkotlin.features.shared.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.seriesappkotlin.core.database.entity.toDomain
import com.example.seriesappkotlin.core.model.Season
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.paging.SeriesPagingSource
import com.example.seriesappkotlin.core.util.NetworkUtils
import com.example.seriesappkotlin.core.util.AppResult
import com.example.seriesappkotlin.data.SeriesApiService
import com.example.seriesappkotlin.features.home.HomeViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartSerieRepository @Inject constructor(
    private val apiRepository: ApiSerieRepository,
    private val roomRepository: RoomSerieRepository,
    private val networkUtils: NetworkUtils,
    private val seriesApiService: SeriesApiService,
    @ApplicationContext private val context: Context
) : SerieRepository {

    companion object {
        private const val TAG = "SmartSerieRepository"
    }

    override fun getPopularSeries(page: Int, genre: String?): Flow<List<Serie>> = flow {
        val cachedFlow = roomRepository.getCachedSeries()
        val cachedData = cachedFlow.first()

        if (cachedData.isNotEmpty()) {
            emit(cachedData)
        }

        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getPopularSeries(page, genre).first()
                if (freshData.isNotEmpty()) {
                    roomRepository.cacheSeries(freshData)
                    emit(freshData)
                }
            } catch (e: Exception) {
                emit(cachedData)
            }
        } else {
            emitAll(cachedFlow)
        }
    }

    override fun getTopRatedSeries(page: Int): Flow<List<Serie>> = flow {
        val cachedData = roomRepository.getCachedSeries().first()
        if (cachedData.isNotEmpty()) {
            emit(cachedData)
        }

        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getTopRatedSeries(page).first()
                if (freshData.isNotEmpty()) {
                    roomRepository.cacheSeries(freshData)
                    emit(freshData)
                }
            } catch (e: Exception) {
                if (cachedData.isEmpty()) throw e
            }
        }
    }

    override fun getDiscoverSeries(page: Int): Flow<List<Serie>> = flow {
        val cachedData = roomRepository.getCachedSeries().first()
        if (cachedData.isNotEmpty()) {
            emit(cachedData)
        }

        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getDiscoverSeries(page).first()
                if (freshData.isNotEmpty()) {
                    roomRepository.cacheSeries(freshData)
                    emit(freshData)
                }
            } catch (e: Exception) {
                if (cachedData.isEmpty()) throw e
            }
        }
    }

    override suspend fun searchSeries(query: String, page: Int): List<Serie> {
        if (query.isBlank()) return emptyList()

        val cachedResults = roomRepository.searchCachedSeries(query)
        if (cachedResults.isNotEmpty()) {
            return cachedResults
        }

        if (networkUtils.isNetworkAvailable()) {
            try {
                val apiResults = apiRepository.searchSeries(query, page)
                if (apiResults.isNotEmpty()) {
                    roomRepository.cacheSeries(apiResults)
                }
                return apiResults
            } catch (e: Exception) {
                return emptyList()
            }
        }
        return emptyList()
    }

    override suspend fun insertSeriesToLocalDb(series: List<Serie>) {
        roomRepository.cacheSeries(series)
    }

    override fun getSeriesFromLocalDb(): Flow<List<Serie>> {
        return roomRepository.getCachedSeries()
    }

    override fun getAllSeriesFromLocalDb(): Flow<List<Serie>> {
        return roomRepository.getAllCachedSeries()
    }

    override suspend fun hasDetailsCached(id: Int): Boolean {
        return roomRepository.getSerieDetail(id) != null
    }

    override fun getPagedSeries(
        sortType: HomeViewModel.SortType,
        searchQuery: String?,
        genreIds: List<Int>?
    ): Flow<PagingData<Serie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                SeriesPagingSource(
                    seriesApiService = seriesApiService,
                    context = context,
                    sortType = sortType,
                    searchQuery = searchQuery,
                    genreIds = genreIds,
                    roomRepository = roomRepository
                )
            }
        ).flow
    }

    override suspend fun getSerieDetails(serieId: Int): AppResult<Serie> {
        val cachedDetail = roomRepository.getSerieDetail(serieId)
        if (cachedDetail != null) {
            return AppResult.Success(cachedDetail.toDomain())
        }

        if (networkUtils.isNetworkAvailable()) {
            val result = apiRepository.getSerieDetails(serieId)
            if (result is AppResult.Success) {
                roomRepository.cacheSerieDetail(result.data)
            }
            return result
        }

        return AppResult.Error(Exception("No network connection and no cached details."))
    }

    override suspend fun getSeasonDetails(
        serieId: Int,
        seasonNumber: Int
    ): AppResult<Season> {
        // Si hay conexión de red, obtener desde la API
        if (networkUtils.isNetworkAvailable()) {
            return apiRepository.getSeasonDetails(serieId, seasonNumber)
        }
    
        // Sin conexión y sin caché
        return AppResult.Error(Exception("No network connection and no cached season details."))
    }
}