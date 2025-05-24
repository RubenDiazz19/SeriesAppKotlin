package com.example.peliculasserieskotlin.features.shared.repository

import androidx.paging.PagingData
import com.example.peliculasserieskotlin.core.database.MediaItemDao
import com.example.peliculasserieskotlin.core.database.toDomain
import com.example.peliculasserieskotlin.core.database.toEntity
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
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

    /**
     * Room no necesita paginación compleja, devuelve datos vacíos
     */
    override fun getPagedMedia(
        mediaType: MediaType,
        sortType: HomeViewModel.SortType,
        searchQuery: String?
    ): Flow<PagingData<MediaItem>> {
        // Room repository no maneja paginación de API
        return flowOf(PagingData.Companion.empty())
    }

    override suspend fun getMovieDetails(movieId: Int): MediaDetailItem? {
        // Room no almacena todos los detalles, así que convertimos un MediaItem básico
        // o devolvemos null si no se encuentra
        val basicItem = mediaItemDao.getMediaById(movieId, MediaType.MOVIE.name)?.toDomain() ?: return null
        
        // Convertimos el MediaItem básico a un MediaDetailItem con campos adicionales nulos
        return MediaDetailItem(
            id = basicItem.id,
            title = basicItem.title,
            overview = basicItem.overview,
            posterUrl = basicItem.posterUrl,
            backdropUrl = basicItem.backdropUrl,
            voteAverage = basicItem.voteAverage,
            type = basicItem.type,
            originalTitle = null,
            releaseDate = null,
            voteCount = null,
            runtime = null,
            budget = null,
            revenue = null,
            tagline = null,
            status = null,
            genres = emptyList(),
            productionCompanies = emptyList(),
            productionCountries = emptyList(),
            spokenLanguages = emptyList()
        )
    }

    override suspend fun getSeriesDetails(seriesId: Int): MediaDetailItem? {
        // Room no almacena todos los detalles, así que convertimos un MediaItem básico
        // o devolvemos null si no se encuentra
        val basicItem = mediaItemDao.getMediaById(seriesId, MediaType.SERIES.name)?.toDomain() ?: return null
        
        // Convertimos el MediaItem básico a un MediaDetailItem con campos adicionales nulos
        return MediaDetailItem(
            id = basicItem.id,
            title = basicItem.title,
            overview = basicItem.overview,
            posterUrl = basicItem.posterUrl,
            backdropUrl = basicItem.backdropUrl,
            voteAverage = basicItem.voteAverage,
            type = basicItem.type,
            originalTitle = null,
            releaseDate = null,
            voteCount = null,
            runtime = null,
            budget = null,
            revenue = null,
            tagline = null,
            status = null,
            genres = emptyList(),
            productionCompanies = emptyList(),
            productionCountries = emptyList(),
            spokenLanguages = emptyList()
        )
    }
}