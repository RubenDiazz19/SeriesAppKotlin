package com.example.peliculasserieskotlin.core.model

data class Episode(
    val id: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    var isWatched: Boolean = false
)