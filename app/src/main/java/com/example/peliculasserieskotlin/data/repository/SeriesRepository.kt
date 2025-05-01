package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface SeriesRepository {
    // Metodo básico para obtener series
    fun getSeries(page: Int, genre: String?): Flow<List<Series>>

    //Metodo para obtener series mejor valoradas
    fun getTopRatedSeries(page: Int): Flow<List<Series>>

    //Metodo para obtener series favoritas
    fun getFavoriteSeries(page: Int): Flow<List<Series>>

    //Metodo para buscar series
    suspend fun searchSeries(query: String): List<Series>

    //Metodo para insertar series en la base de datos local
    suspend fun insertSeries(series: List<Series>)
}