package com.example.seriesappkotlin.features.shared.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.paging.SeriesPagingSource
import com.example.seriesappkotlin.data.SeriesApiService
import com.example.seriesappkotlin.features.home.HomeViewModel
import com.example.seriesappkotlin.core.util.AppResult
import com.example.seriesappkotlin.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiSerieRepository @Inject constructor(
    private val seriesApiService: SeriesApiService,
    @ApplicationContext private val context: Context,
    private val roomRepository: RoomSerieRepository
) : SerieRepository {

    override fun getPopularSeries(page: Int, genre: String?): Flow<List<Serie>> =
        flow {
            val apiKey = context.getString(R.string.apiKey)
            val serieList = seriesApiService.getPopularSeries(apiKey = apiKey, page = page).results.map { it.toSerie() }
            emit(serieList)
        }

    override fun getTopRatedSeries(page: Int): Flow<List<Serie>> = flow {
        val apiKey = context.getString(R.string.apiKey)
        val serieList = seriesApiService.getTopRatedSeries(apiKey = apiKey, page = page).results.map { it.toSerie() }
        emit(serieList)
    }

    override fun getDiscoverSeries(page: Int): Flow<List<Serie>> = flow {
        val apiKey = context.getString(R.string.apiKey)
        val serieList = seriesApiService.getAllSeries(apiKey = apiKey, page = page).results.map { it.toSerie() }
        emit(serieList)
    }

    override suspend fun searchSeries(query: String, page: Int): List<Serie> {
        if (query.isBlank()) return emptyList()
        val apiKey = context.getString(R.string.apiKey)
        return try {
            seriesApiService.searchSeries(query = query, apiKey = apiKey, page = page).results.map { it.toSerie() }
        } catch (e: Exception) {
            Log.e("ApiMediaRepository", "Error en searchSeries: ${e.message}")
            emptyList()
        }
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

    override suspend fun getSerieDetails(serieId: Int): AppResult<Serie> {
        return try {
            val apiKey = context.getString(R.string.apiKey)
            val response = seriesApiService.getSeriesDetails(
                seriesId = serieId,
                apiKey = apiKey,
                appendToResponse = "seasons"
            )
            val serieDetail = response.toDomain()
            AppResult.Success(serieDetail)
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun hasDetailsCached(id: Int): Boolean {
        return false // ApiRepository no maneja caché, siempre retorna false
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
}