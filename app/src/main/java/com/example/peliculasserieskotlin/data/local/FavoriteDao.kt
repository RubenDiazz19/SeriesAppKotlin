package com.example.peliculasserieskotlin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    // Metodo para insertar un favorito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    //Metodo para eliminar un favorito
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    //Metodo para saber si un item es favorito o no
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaId = :mediaId AND mediaType = :mediaType LIMIT 1)")
    fun isFavorite(mediaId: Int, mediaType: String): Flow<Boolean>
    //Metodo para obtener todos los favoritos
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    //Metodo para obtener todos los favoritos por tipo
    @Query("SELECT * FROM favorites WHERE mediaType = :mediaType")
    fun getFavoritesByType(mediaType: String): Flow<List<FavoriteEntity>>
    //Metodo para obtener un favorito por id
    @Query("SELECT * FROM favorites WHERE mediaId = :mediaId AND mediaType = :mediaType")
    fun getFavoriteById(mediaId: Int, mediaType: String): Flow<FavoriteEntity?>
}