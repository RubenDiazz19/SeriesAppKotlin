package com.example.peliculasserieskotlin.domain

import com.example.peliculasserieskotlin.domain.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun insertMovies(movies: List<Movie>)
}