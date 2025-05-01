package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    // Metodo basico para obtener películas
    fun getMovies(page: Int, genre: String?): Flow<List<Movie>>

    //Metodo para obtener películas mejor valoradas
    fun getTopRatedMovies(page: Int): Flow<List<Movie>>

    // Nuevo metodo para obtener películas favoritas
    fun getFavoriteMovies(page: Int): Flow<List<Movie>>

    // Metodo para buscar películas
    suspend fun searchMovies(query: String): List<Movie>

    // Metodo para insertar películas en la base de datos local
    suspend fun insertMovies(movies: List<Movie>)
}