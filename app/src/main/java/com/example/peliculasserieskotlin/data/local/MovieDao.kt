package com.example.peliculasserieskotlin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC")
    fun getTopRatedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE title LIKE :searchQuery OR overview LIKE :searchQuery")
    suspend fun searchMovies(searchQuery: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
}