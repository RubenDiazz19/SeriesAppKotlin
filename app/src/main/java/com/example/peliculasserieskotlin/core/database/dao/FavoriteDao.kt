package com.example.peliculasserieskotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.peliculasserieskotlin.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    // Metodo para insertar un favorito
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    //Metodo para eliminar un favorito
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    //Metodo para saber si un item es favorito o no para un usuario específico
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND mediaId = :mediaId AND mediaType = :mediaType LIMIT 1)")
    fun isFavorite(userId: Int, mediaId: Int, mediaType: String): Flow<Boolean>
    //Metodo para obtener todos los favoritos de un usuario específico
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getAllFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>
    //Metodo para obtener todos los favoritos por tipo para un usuario específico
    @Query("SELECT * FROM favorites WHERE userId = :userId AND mediaType = :mediaType")
    fun getFavoritesByTypeAndUser(userId: Int, mediaType: String): Flow<List<FavoriteEntity>>
    //Metodo para obtener un favorito por id para un usuario específico
    @Query("SELECT * FROM favorites WHERE userId = :userId AND mediaId = :mediaId AND mediaType = :mediaType")
    fun getFavoriteByIdAndUser(userId: Int, mediaId: Int, mediaType: String): Flow<FavoriteEntity?>
    // Métodos legacy para compatibilidad (sin userId) - devuelven lista vacía para invitados
    @Query("SELECT * FROM favorites WHERE userId = -1")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    @Query("SELECT * FROM favorites WHERE userId = -1 AND mediaType = :mediaType")
    fun getFavoritesByType(mediaType: String): Flow<List<FavoriteEntity>>
}