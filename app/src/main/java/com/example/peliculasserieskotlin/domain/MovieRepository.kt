package com.example.peliculasserieskotlin.domain

interface MovieRepository {
    suspend fun getMovies(): List<Movie>
}
