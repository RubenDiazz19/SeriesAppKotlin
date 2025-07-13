package com.example.peliculasserieskotlin.core.model

data class Season(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val episodeCount: Int,
    val posterUrl: String?,
    val episodes: List<Episode> = emptyList()
)