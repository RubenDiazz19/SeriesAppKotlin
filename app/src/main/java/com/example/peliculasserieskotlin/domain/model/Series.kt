package com.example.peliculasserieskotlin.domain.model

data class Series(
    val id: Int,
    val name: String,
    val posterUrl: String,
    val overview: String,
    val year: String,
    val voteAverage: Double
)