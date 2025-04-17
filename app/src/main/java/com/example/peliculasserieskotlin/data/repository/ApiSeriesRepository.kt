package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.domain.model.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiSeriesRepository @Inject constructor(
    private val apiService: SeriesApiService
) : SeriesRepository {

    override fun getSeries(page: Int, genre: String?): Flow<List<Series>> = flow {
        val response = apiService.getSeries("fca0c331c37cedb20821793431ab1389", genre ?: "", page)
        emit(response.results.map { apiModel ->
            Series(
                id = apiModel.id,
                name = apiModel.name ?: "Sin nombre",
                year = apiModel.firstAirDate?.take(4) ?: "Desconocido",
                overview = apiModel.overview ?: "",
                posterUrl = apiModel.posterPath ?: "",
                voteAverage = apiModel.voteAverage ?: 0.0
            )
        })
    }

    override suspend fun insertSeries(series: List<Series>) {
        // Implementa según tu necesidad específica (por ejemplo con Room)
    }
}
