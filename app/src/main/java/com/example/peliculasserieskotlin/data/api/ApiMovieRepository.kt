package com.example.peliculasserieskotlin.data.api

import com.example.peliculasserieskotlin.domain.Movie
import com.example.peliculasserieskotlin.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiMovieRepository(
    private val apiService: MovieApiService
) : MovieRepository {

    private val apiKey = "fca0c331c37cedb20821793431ab1389"

    override fun getMovies(): Flow<List<Movie>> = flow {
        val response = apiService.getPopularMovies(apiKey)
        val movies = response.results.map {
            Movie(
                id = it.id,
                title = it.title,
                year = it.release_date.take(4),
                posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
            )
        }
        emit(movies)
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        // No se usa en API, pero puedes dejarlo vacío
    }
}
