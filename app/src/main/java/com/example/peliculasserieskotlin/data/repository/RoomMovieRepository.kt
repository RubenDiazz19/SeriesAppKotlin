package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.local.MovieDao
import com.example.peliculasserieskotlin.data.local.MovieEntity
import com.example.peliculasserieskotlin.data.repository.MovieRepository
import com.example.peliculasserieskotlin.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomMovieRepository @Inject constructor(
    private val movieDao: MovieDao
) : MovieRepository {


    override fun getMovies(page: Int, genre: String?): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { entity ->
                Movie(
                    id = entity.id,
                    title = entity.title,
                    year = entity.year,
                    posterUrl = entity.posterUrl,
                    overview = entity.overview,
                    voteAverage = entity.voteAverage
                )
            }
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