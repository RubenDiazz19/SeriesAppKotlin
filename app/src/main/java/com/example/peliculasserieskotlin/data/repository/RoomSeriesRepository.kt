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

    override fun getSeries(
        page: Int,
        genre: String?
    ): Flow<List<Series>> {
        return seriesDao.getAllSeries().map { list ->
            list.map { it.toDomain() }
                .filter { series ->
                    genre?.let {
                        series.name.contains(it, ignoreCase = true) ||
                                series.overview.contains(it, ignoreCase = true)
                    } ?: true
                }
        }
    }

    override suspend fun insertSeries(series: List<Series>) {
        seriesDao.insertSeries(series.map { it.toEntity() })
    }

    override suspend fun searchSeries(query: String): List<Series> {
        return seriesDao.searchSeries("%$query%").map {
            it.toDomain()
        }
    }


    // Mapeo de entidad a dominio
    private fun SeriesEntity.toDomain(): Series {
        return Series(
            id = id,
            name = name,
            year = year,
            overview = overview,
            posterUrl = posterUrl,
            voteAverage = voteAverage
        )
    }

    // Mapeo de dominio a entidad
    private fun Series.toEntity(): SeriesEntity {
        return SeriesEntity(
            id = id,
            name = name,
            year = year,
            overview = overview,
            posterUrl = posterUrl,
            voteAverage = voteAverage
        )
    }
}
