package com.example.peliculasserieskotlin.data

import com.example.peliculasserieskotlin.domain.Movie
import com.example.peliculasserieskotlin.domain.MovieRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeMovieRepository @Inject constructor() : MovieRepository {
    override suspend fun getMovies(): List<Movie> {
        delay(1000) // Simula red
        return listOf(
            Movie(1, "Regreso al Futuro", "1985", "https://via.placeholder.com/150"),
            Movie(2, "El Padrino", "1972", "https://via.placeholder.com/150"),
            Movie(3, "Star Wars", "1977", "https://via.placeholder.com/150")
        )
    }
}
