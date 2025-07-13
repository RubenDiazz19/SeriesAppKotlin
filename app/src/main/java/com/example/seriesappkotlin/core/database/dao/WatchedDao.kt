package com.example.seriesappkotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seriesappkotlin.core.database.entity.WatchedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatched(watched: WatchedEntity)

    @Query("DELETE FROM watched_series WHERE userId = :userId AND serieId = :serieId")
    suspend fun deleteWatched(userId: Int, serieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watched_series WHERE userId = :userId AND serieId = :serieId LIMIT 1)")
    fun isWatched(userId: Int, serieId: Int): Flow<Boolean>

    @Query("SELECT * FROM watched_series WHERE userId = :userId")
    fun getAllWatchedByUser(userId: Int): Flow<List<WatchedEntity>>

    @Query("SELECT * FROM watched_series")
    fun getAllWatched(): Flow<List<WatchedEntity>>
}