package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.local.MovieDao
import com.example.peliculasserieskotlin.data.local.MovieEntity
import com.example.peliculasserieskotlin.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomMovieRepository @Inject constructor(
    private val movieDao: MovieDao
) : MovieRepository {

    override fun getMovies(page: Int, genre: String?): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopRatedMovies(page: Int): Flow<List<Movie>> {
        return movieDao.getTopRatedMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteMovies(page: Int): Flow<List<Movie>> {
        // En una implementación real, esto obtendría las películas marcadas como favoritas
        // Por ahora devolvemos todas las películas como ejemplo
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        movieDao.insertMovies(movies.map { it.toEntity() })
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return movieDao.searchMovies("%$query%").map { it.toDomain() }
    }

    private fun MovieEntity.toDomain(): Movie {
        return Movie(
            id = id,
            title = title,
            year = year,
            posterUrl = posterUrl,
            overview = overview,
            voteAverage = voteAverage
        )
    }

    private fun Movie.toEntity(): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            year = year,
            posterUrl = posterUrl,
            overview = overview,
            voteAverage = voteAverage
        )
    }
}