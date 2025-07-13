package com.example.seriesappkotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seriesappkotlin.core.database.entity.SerieEntity
import com.example.seriesappkotlin.core.database.entity.SerieDetailEntity
import com.example.seriesappkotlin.core.database.entity.SeasonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SerieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: List<SerieEntity>)

    @Query("SELECT * FROM series ORDER BY title ASC")
    fun getAllSeries(): Flow<List<SerieEntity>>

    @Query("SELECT * FROM series ORDER BY voteAverage DESC")
    fun getTopRatedSeries(): Flow<List<SerieEntity>>

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun getSerieById(id: Int): SerieEntity?

    @Query("SELECT * FROM series WHERE id IN (:ids)")
    suspend fun getSeriesByIds(ids: List<Int>): List<SerieEntity>

    @Query("SELECT * FROM series WHERE title LIKE '%' || :query || '%' OR overview LIKE '%' || :query || '%'")
    suspend fun searchSeries(query: String): List<SerieEntity>

    @Query("DELETE FROM series")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSerieDetail(serieDetail: SerieDetailEntity)

    @Query("SELECT * FROM series_details WHERE id = :id")
    suspend fun getSerieDetailById(id: Int): SerieDetailEntity?

    @Query("DELETE FROM series_details WHERE id = :id")
    suspend fun deleteSerieDetail(id: Int)

    @Query("DELETE FROM series_details")
    suspend fun clearAllDetails()

    @Query("SELECT * FROM series")
    suspend fun getAllSeriesList(): List<SerieEntity>

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Métodos para Season
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: SeasonEntity)
    
    @Query("SELECT * FROM seasons WHERE serieId = :serieId AND seasonNumber = :seasonNumber LIMIT 1")
    suspend fun getSeasonDetails(serieId: Int, seasonNumber: Int): SeasonEntity?
    
    @Query("DELETE FROM seasons WHERE serieId = :serieId AND seasonNumber = :seasonNumber")
    suspend fun deleteSeasonDetails(serieId: Int, seasonNumber: Int)
    
    @Query("SELECT * FROM seasons WHERE serieId = :serieId")
    suspend fun getAllSeasonsForSerie(serieId: Int): List<SeasonEntity>
}