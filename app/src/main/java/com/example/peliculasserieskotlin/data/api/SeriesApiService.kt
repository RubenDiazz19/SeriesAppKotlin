// SeriesApiService.kt
package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.data.api.model.SeriesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeriesApiService {
    @GET("discover/tv")
    suspend fun getSeries(
        @Query("api_key") apiKey: String,
        @Query("genre") genre: String,
        @Query("page") page: Int
    ): SeriesApiResponse
}
