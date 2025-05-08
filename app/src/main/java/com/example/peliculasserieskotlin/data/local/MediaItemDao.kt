package com.example.peliculasserieskotlin.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items WHERE mediaType = :mediaType ORDER BY title ASC")
    fun getAllMedia(mediaType: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE mediaType = :mediaType ORDER BY voteAverage DESC LIMIT :limit")
    fun getTopRatedMedia(mediaType: String, limit: Int = 20): Flow<List<MediaItemEntity>>
    
    @Query("SELECT * FROM media_items WHERE mediaType = :mediaType ORDER BY id DESC LIMIT :limit")
    fun getPopularMedia(mediaType: String, limit: Int = 20): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE mediaType = :mediaType AND (title LIKE '%' || :searchQuery || '%' OR overview LIKE '%' || :searchQuery || '%')")
    suspend fun searchMedia(searchQuery: String, mediaType: String): List<MediaItemEntity>
    
    @Query("SELECT * FROM media_items WHERE id = :itemId AND mediaType = :mediaType")
    suspend fun getMediaById(itemId: Int, mediaType: String): MediaItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<MediaItemEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity)
    
    @Update
    suspend fun updateMediaItem(item: MediaItemEntity)
    
    @Delete
    suspend fun deleteMediaItem(item: MediaItemEntity)
    
    @Query("DELETE FROM media_items WHERE mediaType = :mediaType")
    suspend fun deleteAllMediaByType(mediaType: String)
    
    @Query("DELETE FROM media_items")
    suspend fun deleteAllMedia()
}