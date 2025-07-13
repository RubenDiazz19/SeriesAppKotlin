package com.example.seriesappkotlin.data

import com.example.seriesappkotlin.data.model.SeriesApiResponse
import com.example.seriesappkotlin.data.model.responses.SeriesDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
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

    /**
     * Obtiene los detalles completos de una serie específica.
     * @param seriesId ID de la serie a consultar
     * @param apiKey Clave de API para TMDB
     * @param language Idioma de los resultados (por defecto es-ES)
     * @param appendToResponse Información adicional a incluir (videos, créditos, etc.)
     * @return Detalles completos de la serie
     */
    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("append_to_response") appendToResponse: String? = null
    ): SeriesDetailResponse
}