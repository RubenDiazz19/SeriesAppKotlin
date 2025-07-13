package com.example.seriesappkotlin.core.model

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
    GenreItem(16, "Animación"),
    GenreItem(35, "Comedia"),
    GenreItem(80, "Crimen"),
    GenreItem(18, "Drama"),
    GenreItem(10751, "Familiar"),
) 