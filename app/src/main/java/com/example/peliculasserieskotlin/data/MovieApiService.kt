package com.example.peliculasserieskotlin.data

import com.example.peliculasserieskotlin.data.model.MovieApiResponse
import com.example.peliculasserieskotlin.data.model.responses.MovieDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
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
    
    /**
     * Obtiene los detalles completos de una película específica.
     * @param movieId ID de la película a consultar
     * @param apiKey Clave de API para TMDB
     * @param language Idioma de los resultados (por defecto es-ES)
     * @param appendToResponse Información adicional a incluir (videos, créditos, etc.)
     * @return Detalles completos de la película
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("append_to_response") appendToResponse: String? = null
    ): MovieDetailResponse
}