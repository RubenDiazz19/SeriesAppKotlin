package com.example.peliculasserieskotlin.presentation.home

import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.domain.model.Series

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val series: List<Series> = emptyList(),
    val isLoading: Boolean = false
)
