package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.data.api.model.SeriesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para acceder a los endpoints de series de TMDB.
 */
interface SeriesApiService {
    /**
     * Obtiene listado de series con filtros.
     */
    @GET("discover/tv")
    suspend fun getAllSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    /**
     * Busca series por texto.
     */
    @GET("search/tv")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    /**
     * Obtiene series mejor valoradas.
     */
    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    /**
     * Obtiene series populares.
     */
    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse
}