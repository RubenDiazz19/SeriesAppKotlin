package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.data.api.model.SeriesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeriesApiService {

    //Metodo para obtener todas las series
    @GET("discover/tv")
    suspend fun getAllSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    //Metodo para buscar series
    @GET("search/tv")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    //Metodo para obtener las series más valoradas
    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse

    //Metodo para obtener las series populares (podría usarse para favoritos)
    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): SeriesApiResponse
}