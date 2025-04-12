package com.example.peliculasserieskotlin.data

import com.example.peliculasserieskotlin.domain.Movie
import com.example.peliculasserieskotlin.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeMovieRepository @Inject constructor() : MovieRepository {

    private val fakeMovies = mutableListOf(
        Movie(1, "Regreso al Futuro", "1985", "https://via.placeholder.com/150"),
        Movie(2, "El Padrino", "1972", "https://via.placeholder.com/150"),
        Movie(3, "Star Wars", "1977", "https://via.placeholder.com/150")
    )

    override fun getMovies(): Flow<List<Movie>> {
        return flow { emit(fakeMovies.toList()) }
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        fakeMovies.addAll(movies)
    }
}
