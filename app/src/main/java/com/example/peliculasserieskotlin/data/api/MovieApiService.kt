package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.data.api.model.MovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    // Metodo para obtener todas las películas
    @GET("discover/movie")
    suspend fun getAllMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse

    // Metodo para buscar películas
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse
}
