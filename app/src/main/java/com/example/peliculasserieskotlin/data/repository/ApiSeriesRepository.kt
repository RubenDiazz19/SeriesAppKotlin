package com.example.peliculasserieskotlin.data.repository

import android.content.Context
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.api.model.toDomain
import com.example.peliculasserieskotlin.domain.model.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiSeriesRepository @Inject constructor(
    private val api: SeriesApiService,
    private val context: Context
) : SeriesRepository {

    override fun getSeries(page: Int, genre: String?): Flow<List<Series>> = flow {
        try {
            val apiKey = context.getString(R.string.apiKey)
            val response = api.getAllSeries(
                apiKey = apiKey,
                page = page
            )
            val series = response.results.map { it.toDomain() }
            val filtered = genre?.let { g ->
                series.filter {
                    it.name.contains(g, ignoreCase = true) || it.overview.contains(g, ignoreCase = true)
                }
            } ?: series
            emit(filtered)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getTopRatedSeries(page: Int): Flow<List<Series>> = flow {
        try {
            val apiKey = context.getString(R.string.apiKey)
            val response = api.getTopRatedSeries(
                apiKey = apiKey,
                page = page
            )
            val series = response.results.map { it.toDomain() }
            emit(series)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getFavoriteSeries(page: Int): Flow<List<Series>> = flow {
        // In a real implementation, this could come from a local database
        // that stores the user's favorite series IDs.
        // For now, just return normal series as an example.
        try {
            val apiKey = context.getString(R.string.apiKey)
            val response = api.getAllSeries(apiKey = apiKey, page = page)
            val series = response.results.map { it.toDomain() }
            emit(series)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun searchSeries(query: String): List<Series> {
        return try {
            val apiKey = context.getString(R.string.apiKey)
            val response = api.searchSeries(query, apiKey)
            response.results.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun insertSeries(series: List<Series>) {
        TODO("Not yet implemented")
    }
}