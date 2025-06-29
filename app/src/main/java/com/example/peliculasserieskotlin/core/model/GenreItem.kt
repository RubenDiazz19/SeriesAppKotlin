package com.example.peliculasserieskotlin.core.model

/**
 * Representa un género con id y nombre.
 */
data class GenreItem(
    val id: Int,
    val name: String
)

/**
 * Lista de géneros soportados (ID y nombre), para películas y series.
 * Puedes perfeccionarla o dividirla más adelante si lo necesitas.
 */
val SUPPORTED_GENRES = listOf(
    GenreItem(28, "Acción"),
    GenreItem(12, "Aventura"),
    GenreItem(16, "Animación"),
    GenreItem(35, "Comedia"),
    GenreItem(80, "Crimen"),
    GenreItem(18, "Drama"),
    GenreItem(10751, "Familiar"),
    GenreItem(14, "Fantasía"),
    GenreItem(36, "Historia"),
    GenreItem(10749, "Romance"),
    GenreItem(878, "Ciencia ficción"),
    GenreItem(10752, "Bélica"),
    GenreItem(37, "Western")
) 