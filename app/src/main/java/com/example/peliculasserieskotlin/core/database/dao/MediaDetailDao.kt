package com.example.peliculasserieskotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.peliculasserieskotlin.core.database.entity.MediaDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar los detalles de elementos multimedia en la base de datos.
 */
@Dao
interface MediaDetailDao {

    /**
     * Obtiene los detalles de un elemento por ID y tipo.
     */
    @Query(
        """
        SELECT * FROM media_details
        WHERE id = :itemId AND mediaType = :mediaType
        LIMIT 1
        """
    )
    suspend fun getDetailById(itemId: Int, mediaType: String): MediaDetailEntity?

    /**
     * Obtiene los detalles de un elemento por ID y tipo como Flow.
     */
    @Query(
        """
        SELECT * FROM media_details
        WHERE id = :itemId AND mediaType = :mediaType
        LIMIT 1
        """
    )
    fun getDetailByIdFlow(itemId: Int, mediaType: String): Flow<MediaDetailEntity?>

    /**
     * Obtiene todos los detalles de un tipo específico.
     */
    @Query(
        """
        SELECT * FROM media_details
        WHERE mediaType = :mediaType
        ORDER BY lastUpdated DESC
        """
    )
    fun getAllDetailsByType(mediaType: String): Flow<List<MediaDetailEntity>>

    /**
     * Obtiene todos los detalles almacenados.
     */
    @Query("SELECT * FROM media_details ORDER BY lastUpdated DESC")
    fun getAllDetails(): Flow<List<MediaDetailEntity>>

    /**
     * Inserta o actualiza los detalles de un elemento.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: MediaDetailEntity)

    /**
     * Inserta o actualiza una lista de detalles.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<MediaDetailEntity>)

    /**
     * Elimina los detalles de un elemento específico.
     */
    @Query("DELETE FROM media_details WHERE id = :itemId AND mediaType = :mediaType")
    suspend fun deleteDetail(itemId: Int, mediaType: String)

    /**
     * Elimina todos los detalles de un tipo específico.
     */
    @Query("DELETE FROM media_details WHERE mediaType = :mediaType")
    suspend fun deleteAllDetailsByType(mediaType: String)

    /**
     * Elimina todos los detalles almacenados.
     */
    @Query("DELETE FROM media_details")
    suspend fun deleteAllDetails()

    /**
     * Obtiene la fecha de última actualización de un elemento.
     */
    @Query(
        """
        SELECT lastUpdated FROM media_details
        WHERE id = :itemId AND mediaType = :mediaType
        LIMIT 1
        """
    )
    suspend fun getLastUpdated(itemId: Int, mediaType: String): Long?

    /**
     * Verifica si existe un detalle en caché.
     */
    @Query(
        """
        SELECT COUNT(*) FROM media_details
        WHERE id = :itemId AND mediaType = :mediaType
        """
    )
    suspend fun existsDetail(itemId: Int, mediaType: String): Int

    /**
     * Obtiene todos los detalles de la tabla (sin Flow).
     */
    @Query("SELECT * FROM media_details")
    suspend fun getAllDetailsList(): List<MediaDetailEntity>

    /**
     * Borra un detalle por id y mediaType.
     */
    @Query("DELETE FROM media_details WHERE id = :itemId AND mediaType = :mediaType")
    suspend fun deleteByIdAndType(itemId: Int, mediaType: String)
} 