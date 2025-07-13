package com.example.peliculasserieskotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND mediaId = :mediaId")
    suspend fun deleteFavorite(userId: Int, mediaId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND mediaId = :mediaId LIMIT 1)")
    fun isFavorite(userId: Int, mediaId: Int): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getAllFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
}