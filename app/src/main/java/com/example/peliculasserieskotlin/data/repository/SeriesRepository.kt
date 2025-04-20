package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface SeriesRepository {
    fun getSeries(page: Int, genre: String?): Flow<List<Series>>
    suspend fun insertSeries(series: List<Series>)
    suspend fun searchSeries(query: String): List<Series>
}
