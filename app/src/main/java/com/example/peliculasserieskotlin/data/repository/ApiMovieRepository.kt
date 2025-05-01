package com.example.peliculasserieskotlin.data.repository

import android.content.Context
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.model.toDomain
import com.example.peliculasserieskotlin.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiMovieRepository @Inject constructor(
    private val api: MovieApiService,
    private val context: Context
) : MovieRepository {

    override fun getMovies(page: Int, genre: String?): Flow<List<Movie>> = flow {
        try {
            val apiKey = context.getString(R.string.apiKey)

            val response = api.getAllMovies(
                apiKey = apiKey,
                page = page
            )

            val movies = response.results.map { it.toDomain() }

            val filtered = genre?.let { g ->
                movies.filter {
                    it.title.contains(g, ignoreCase = true) || it.overview.contains(g, ignoreCase = true)
                }
            } ?: movies

            emit(filtered)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getTopRatedMovies(page: Int): Flow<List<Movie>> = flow {
        try {
            val apiKey = context.getString(R.string.apiKey)

            val response = api.getTopRatedMovies(
                apiKey = apiKey,
                page = page
            )

            val movies = response.results.map { it.toDomain() }
            emit(movies)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getFavoriteMovies(page: Int): Flow<List<Movie>> = flow {
        // En una implementación real, esto podría venir de una base de datos local
        // que almacene los IDs de las películas favoritas del usuario
        // Por ahora, simplemente devolvemos películas normales como ejemplo
        try {
            val apiKey = context.getString(R.string.apiKey)
            val response = api.getAllMovies(apiKey = apiKey, page = page)
            val movies = response.results.map { it.toDomain() }
            emit(movies)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return try {
            val apiKey = context.getString(R.string.apiKey)

            val response = api.searchMovies(query, apiKey)
            response.results.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        // No se usa, ya que vienen desde la API
    }
}