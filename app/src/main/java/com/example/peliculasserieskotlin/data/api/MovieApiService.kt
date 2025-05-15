package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.data.api.model.MovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para acceder a los endpoints de películas de TMDB.
 */
interface MovieApiService {

    /**
     * Obtiene listado de películas con filtros.
     */
    @GET("discover/movie")
    suspend fun getAllMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse

    /**
     * Busca películas por texto.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse

    /**
     * Obtiene películas mejor valoradas.
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse

    /**
     * Obtiene películas populares.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieApiResponse
}