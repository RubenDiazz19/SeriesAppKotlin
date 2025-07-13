package com.example.seriesappkotlin.features.seasondetail

import com.example.seriesappkotlin.core.model.Episode

data class SeasonDetailUiState(
    val name: String = "",
    val overview: String? = null,
    val posterUrl: String? = null,
    val airDate: String? = null,
    val episodes: List<Episode>? = null,
    val seasonNumber: Int = 0,
    val error: String? = null
)