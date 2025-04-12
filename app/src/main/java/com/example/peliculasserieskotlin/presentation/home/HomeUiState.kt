package com.example.peliculasserieskotlin.presentation.home

import com.example.peliculasserieskotlin.domain.Movie

data class HomeUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val errorMessage: String? = null
)
