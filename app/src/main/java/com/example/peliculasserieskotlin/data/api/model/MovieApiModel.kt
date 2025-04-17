// MovieApiModel.kt
package com.example.peliculasserieskotlin.data.api.model

data class MovieApiModel(
    val id: Int?,
    val title: String?,
    val release_date: String?,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?
)

data class MovieApiResponse(
    val results: List<MovieApiModel>
)


