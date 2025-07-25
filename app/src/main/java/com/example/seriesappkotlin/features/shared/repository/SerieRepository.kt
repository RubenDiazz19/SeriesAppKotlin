package com.example.seriesappkotlin.features.shared.repository

import androidx.paging.PagingData
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.features.home.HomeViewModel
import kotlinx.coroutines.flow.Flow
import com.example.seriesappkotlin.core.util.AppResult

interface SerieRepository {

    fun getPopularSeries(page: Int, genre: String?): Flow<List<Serie>>

    fun getTopRatedSeries(page: Int): Flow<List<Serie>>

    fun getDiscoverSeries(page: Int): Flow<List<Serie>>

    suspend fun searchSeries(query: String, page: Int): List<Serie>

    suspend fun insertSeriesToLocalDb(series: List<Serie>)

    fun getSeriesFromLocalDb(): Flow<List<Serie>>

    fun getAllSeriesFromLocalDb(): Flow<List<Serie>>

    suspend fun hasDetailsCached(id: Int): Boolean

    fun getPagedSeries(
        sortType: HomeViewModel.SortType,
        searchQuery: String?,
        genreIds: List<Int>?
    ): Flow<PagingData<Serie>>

    suspend fun getSerieDetails(serieId: Int): AppResult<Serie>

    suspend fun getSeasonDetails(serieId: Int, seasonNumber: Int): AppResult<com.example.seriesappkotlin.core.model.Season>
}