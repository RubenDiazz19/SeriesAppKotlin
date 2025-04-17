package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.domain.model.Movie
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiMovieRepository @Inject constructor(
    private val api: MovieApiService
) : MovieRepository {

    override fun getMovies(
        page: Int,
        genre: String?
    ): Flow<List<Movie>> = flow {
        val response = api.getAllMovies(
            apiKey = "fca0c331c37cedb20821793431ab1389",
            page = page
        )

        val moviesList = response.results.mapNotNull { result ->
            val id = result.id ?: return@mapNotNull null
            val title = result.title ?: "Título desconocido"
            val year = result.release_date?.split("-")?.getOrNull(0) ?: "Año desconocido"
            val overview = result.overview ?: "Sin descripción"
            val posterUrl = result.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: ""
            val voteAverage = result.vote_average ?: 0.0

            Movie(
                id = id,
                title = title,
                year = year,
                overview = overview,
                posterUrl = posterUrl,
                voteAverage = voteAverage
            )
        }

        // Aplicar filtro por género (opcional)
        val filtered = genre?.let { g ->
            moviesList.filter {
                it.title.contains(g, ignoreCase = true) ||
                        it.overview.contains(g, ignoreCase = true)
            }
        } ?: moviesList

        emit(filtered)
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        // No se guarda en base de datos local
    }
}
