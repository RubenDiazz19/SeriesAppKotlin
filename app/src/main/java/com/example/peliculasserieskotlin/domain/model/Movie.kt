package com.example.peliculasserieskotlin.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val year: String,
    val overview: String,
    val posterUrl: String,
    val voteAverage: Double
)
