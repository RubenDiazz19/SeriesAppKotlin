package com.example.peliculasserieskotlin.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar los elementos multimedia en la base de datos.
 * Proporciona métodos para consultar e insertar películas y series.
 */
@Dao
interface MediaItemDao {

    /*────────────────────────  SELECT  ────────────────────────*/

    /**
     * Obtiene todos los elementos de un tipo específico.
     */
    @Query(
        """
        SELECT * FROM media_items
        WHERE LOWER(mediaType) = LOWER(:mediaType)
        ORDER BY title ASC
        """
    )
    fun getAllMedia(mediaType: String): Flow<List<MediaItemEntity>>

    /**
     * Obtiene elementos populares por tipo.
     */
    @Query(
        """
        SELECT * FROM media_items
        WHERE LOWER(mediaType) = LOWER(:mediaType)
        ORDER BY id DESC
        """
    )
    fun getPopularMedia(mediaType: String): Flow<List<MediaItemEntity>>

    /**
     * Obtiene elementos mejor valorados por tipo.
     */
    @Query(
        """
        SELECT * FROM media_items
        WHERE LOWER(mediaType) = LOWER(:mediaType)
        ORDER BY voteAverage DESC
        """
    )
    fun getTopRatedMedia(mediaType: String): Flow<List<MediaItemEntity>>

    /**
     * Busca elementos por texto en título o descripción.
     */
    @Query(
        """
        SELECT * FROM media_items
        WHERE LOWER(mediaType) = LOWER(:mediaType)
          AND (title LIKE '%' || :query || '%' OR overview LIKE '%' || :query || '%')
        """
    )
    suspend fun searchMedia(query: String, mediaType: String): List<MediaItemEntity>

    /**
     * Obtiene un elemento por ID y tipo.
     */
    @Query(
        """
        SELECT * FROM media_items
        WHERE id = :itemId
          AND LOWER(mediaType) = LOWER(:mediaType)
        LIMIT 1
        """
    )
    suspend fun getMediaById(itemId: Int, mediaType: String): MediaItemEntity?

    /**
     * Obtiene elementos por lista de IDs (para favoritos).
     */
    @Query("SELECT * FROM media_items WHERE id IN (:ids)")
    suspend fun getMediaItemsByIds(ids: List<Int>): List<MediaItemEntity>

    /*──────────────────────  INSERT / UPDATE  ───────────────────────*/

    /**
     * Inserta un elemento multimedia.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity)

    /**
     * Inserta una lista de elementos multimedia.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<MediaItemEntity>)
}
