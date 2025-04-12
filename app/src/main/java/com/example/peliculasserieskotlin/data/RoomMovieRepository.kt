package com.example.peliculasserieskotlin.data

import com.example.peliculasserieskotlin.domain.Movie
import com.example.peliculasserieskotlin.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomMovieRepository @Inject constructor(
    private val movieDao: MovieDao
) : MovieRepository {

    override fun getMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertMovies(movies: List<Movie>) {
        movieDao.insertMovies(movies.map { it.toEntity() })
    }

    private fun MovieEntity.toDomain(): Movie {
        return Movie(
            id = id,
            title = title,
            year = year,
            posterUrl = posterUrl
        )
    }

    private fun Movie.toEntity(): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            year = year,
            posterUrl = posterUrl
        )
    }
}