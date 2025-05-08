package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.data.local.MediaItemDao
import com.example.peliculasserieskotlin.data.local.toDomain
import com.example.peliculasserieskotlin.data.local.toEntity
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomMediaRepository @Inject constructor(
    private val mediaItemDao: MediaItemDao
) : MediaRepository {

    override fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>> {
        return mediaItemDao.getPopularMedia(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTopRatedMedia(page: Int, type: MediaType): Flow<List<MediaItem>> {
        return mediaItemDao.getTopRatedMedia(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDiscoverMedia(page: Int, type: MediaType): Flow<List<MediaItem>> {
        return mediaItemDao.getAllMedia(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun searchMedia(query: String, page: Int, type: MediaType): List<MediaItem> {
        return mediaItemDao.searchMedia(query, type.name).map { it.toDomain() }
    }

    override suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>) {
        mediaItemDao.insertMediaItems(mediaItems.map { it.toEntity() })
    }

    override fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>> {
        return mediaItemDao.getAllMedia(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }


    override fun getAllMediaFromLocalDb(): Flow<List<MediaItem>> {
        // Obtenemos tanto películas como series y las combinamos
        val movies = mediaItemDao.getAllMedia(MediaType.MOVIE.name)
        val series = mediaItemDao.getAllMedia(MediaType.SERIES.name)
        
        return movies.combine(series) { movieList, seriesList ->
            movieList.map { it.toDomain() } + seriesList.map { it.toDomain() }
        }
    }

    suspend fun getMediaDetails(id: Int, type: MediaType): MediaItem? {
        return mediaItemDao.getMediaById(id, type.name)?.toDomain()
    }

    suspend fun saveMediaItems(items: List<MediaItem>) {
        mediaItemDao.insertMediaItems(items.map { it.toEntity() })
    }
}