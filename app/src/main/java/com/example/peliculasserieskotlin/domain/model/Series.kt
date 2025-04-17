// Series.kt
package com.example.peliculasserieskotlin.domain.model

data class Series(
    val id: Int,
    val name: String,
    val year: String,
    val overview: String,
    val posterUrl: String,
    val voteAverage: Double
)

