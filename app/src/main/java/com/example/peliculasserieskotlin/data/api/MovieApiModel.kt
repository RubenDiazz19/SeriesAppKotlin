package com.example.peliculasserieskotlin.data.api

data class MovieApiResponse(
    val results: List<MovieApiModel>
)

data class MovieApiModel(
    val id: Int,
    val title: String,
    val release_date: String,
    val poster_path: String?
)
