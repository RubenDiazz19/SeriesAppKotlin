package com.example.seriesappkotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seriesappkotlin.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_series WHERE userId = :userId AND serieId = :serieId")
    suspend fun deleteFavorite(userId: Int, serieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_series WHERE userId = :userId AND serieId = :serieId LIMIT 1)")
    fun isFavorite(userId: Int, serieId: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_series WHERE userId = :userId")
    fun getAllFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite_series")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
}