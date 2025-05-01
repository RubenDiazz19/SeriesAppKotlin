package com.example.peliculasserieskotlin.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val year: String,
    val posterUrl: String,
    val overview: String,
    val voteAverage: Double
)