package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.local.SeriesDao
import com.example.peliculasserieskotlin.data.local.SeriesEntity
import com.example.peliculasserieskotlin.domain.model.Series
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSeriesRepository @Inject constructor(
    private val seriesDao: SeriesDao
) : SeriesRepository {

    override fun getSeries(page: Int, genre: String?): Flow<List<Series>> {
        return seriesDao.getAllSeries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopRatedSeries(page: Int): Flow<List<Series>> {
        return seriesDao.getTopRatedSeries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteSeries(page: Int): Flow<List<Series>> {
        // En una implementación real, esto obtendría las series marcadas como favoritas
        // Por ahora devolvemos todas las series como ejemplo
        return seriesDao.getAllSeries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSeries(series: List<Series>) {
        seriesDao.insertSeries(series.map { it.toEntity() })
    }

    override suspend fun searchSeries(query: String): List<Series> {
        return seriesDao.searchSeries("%$query%").map { it.toDomain() }
    }

    private fun SeriesEntity.toDomain(): Series {
        return Series(
            id = id,
            name = name,
            posterUrl = posterUrl,
            overview = overview,
            voteAverage = voteAverage,
            year = year
        )
    }

    private fun Series.toEntity(): SeriesEntity {
        return SeriesEntity(
            id = id,
            name = name,
            posterUrl = posterUrl,
            overview = overview,
            voteAverage = voteAverage,
            year = year
        )
    }
}