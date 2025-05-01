package com.example.peliculasserieskotlin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series")
    fun getAllSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series ORDER BY voteAverage DESC")
    fun getTopRatedSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE name LIKE :searchQuery OR overview LIKE :searchQuery")
    suspend fun searchSeries(searchQuery: String): List<SeriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: List<SeriesEntity>)
}