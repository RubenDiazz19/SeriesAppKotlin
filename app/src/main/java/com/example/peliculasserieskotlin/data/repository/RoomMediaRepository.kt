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

/**
 * Repositorio que gestiona el acceso a la base de datos local Room.
 * Implementa MediaRepository para proporcionar acceso a contenido multimedia almacenado localmente.
 */
class RoomMediaRepository @Inject constructor(
    private val mediaItemDao: MediaItemDao
) : MediaRepository {

    /**
     * Obtiene medios populares desde la base de datos local.
     */
    override fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>> =
        mediaItemDao.getPopularMedia(type.name).map { it.map { e -> e.toDomain() } }

    /**
     * Obtiene medios mejor valorados desde la base de datos local.
     */
    override fun getTopRatedMedia(page: Int, type: MediaType): Flow<List<MediaItem>> =
        mediaItemDao.getTopRatedMedia(type.name).map { it.map { e -> e.toDomain() } }

    /**
     * Obtiene todos los medios de un tipo específico desde la base de datos local.
     */
    override fun getDiscoverMedia(page: Int, type: MediaType): Flow<List<MediaItem>> =
        mediaItemDao.getAllMedia(type.name).map { it.map { e -> e.toDomain() } }

    /**
     * Busca medios por texto en la base de datos local.
     */
    override suspend fun searchMedia(query: String, page: Int, type: MediaType): List<MediaItem> =
        mediaItemDao.searchMedia(query, type.name).map { it.toDomain() }

    /**
     * Inserta elementos multimedia en la base de datos local.
     */
    override suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>) =
        mediaItemDao.insertMediaItems(mediaItems.map { it.toEntity() })

    /**
     * Obtiene todos los medios de un tipo específico desde la base de datos local.
     */
    override fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>> =
        mediaItemDao.getAllMedia(type.name).map { it.map { e -> e.toDomain() } }

    /**
     * Obtiene todos los medios (películas y series) desde la base de datos local.
     */
    override fun getAllMediaFromLocalDb(): Flow<List<MediaItem>> {
        val movies = mediaItemDao.getAllMedia(MediaType.MOVIE.name)
        val series = mediaItemDao.getAllMedia(MediaType.SERIES.name)
        return movies.combine(series) { m, s -> m.map { it.toDomain() } + s.map { it.toDomain() } }
    }

    /**
     * Obtiene detalles de un elemento multimedia específico por ID y tipo.
     */
    suspend fun getMediaDetails(id: Int, type: MediaType): MediaItem? =
        mediaItemDao.getMediaById(id, type.name)?.toDomain()

    /**
     * Guarda una lista de elementos multimedia en la base de datos local.
     */
    suspend fun saveMediaItems(items: List<MediaItem>) =
        mediaItemDao.insertMediaItems(items.map { it.toEntity() })
}
