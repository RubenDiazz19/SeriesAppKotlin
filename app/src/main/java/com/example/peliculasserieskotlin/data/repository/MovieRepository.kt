package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(page: Int, genre: String?): Flow<List<Movie>>
    suspend fun insertMovies(movies: List<Movie>)
    suspend fun searchMovies(query: String): List<Movie>
}
