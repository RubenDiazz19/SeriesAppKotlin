package com.example.peliculasserieskotlin.features.shared.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.paging.MediaPagingSource
import com.example.peliculasserieskotlin.data.MovieApiService
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import com.example.peliculasserieskotlin.core.util.AppResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApiMediaRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val seriesApiService: SeriesApiService,
    @ApplicationContext private val context: Context,
    private val roomRepository: RoomMediaRepository
) : MediaRepository {

    /**
     * Obtiene medios populares desde la API.
     * @param page Número de página para paginación.
     * @param genre Género (no utilizado en esta implementación).
     * @param type Tipo de medio (MOVIE o SERIES).
     * @return Flow con lista de elementos multimedia.
     */
    override fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>> =
        flow {
            val apiKey = context.getString(R.string.apiKey)
            val mediaList = when (type) {
                MediaType.MOVIE -> movieApiService.getPopularMovies(apiKey = apiKey, page = page).results.map { it.toDomain() }
                MediaType.SERIES -> seriesApiService.getPopularSeries(apiKey = apiKey, page = page).results.map { it.toDomain() }
            }
            emit(mediaList)
        }

    /**
     * Obtiene medios mejor valorados desde la API.
     * @param page Número de página para paginación.
     * @param type Tipo de medio (MOVIE o SERIES).
     * @return Flow con lista de elementos multimedia.
     */
    override fun getTopRatedMedia(page: Int, type: MediaType): Flow<List<MediaItem>> = flow {
        val apiKey = context.getString(R.string.apiKey)
        val mediaList = when (type) {
            MediaType.MOVIE -> movieApiService.getTopRatedMovies(apiKey = apiKey, page = page).results.map { it.toDomain() }
            MediaType.SERIES -> seriesApiService.getTopRatedSeries(apiKey = apiKey, page = page).results.map { it.toDomain() }
        }
        emit(mediaList)
    }

    /**
     * Obtiene medios de descubrimiento desde la API.
     * @param page Número de página para paginación.
     * @param type Tipo de medio (MOVIE o SERIES).
     * @return Flow con lista de elementos multimedia.
     */
    override fun getDiscoverMedia(page: Int, type: MediaType): Flow<List<MediaItem>> = flow {
        val apiKey = context.getString(R.string.apiKey)
        val mediaList = when (type) {
            MediaType.MOVIE -> movieApiService.getAllMovies(apiKey = apiKey, page = page).results.map { it.toDomain() }
            MediaType.SERIES -> seriesApiService.getAllSeries(apiKey = apiKey, page = page).results.map { it.toDomain() }
        }
        emit(mediaList)
    }

    /**
     * Busca medios por texto en la API.
     * @param query Texto de búsqueda.
     * @param page Número de página para paginación.
     * @param type Tipo de medio (MOVIE o SERIES).
     * @return Lista de elementos multimedia que coinciden con la búsqueda.
     */
    override suspend fun searchMedia(query: String, page: Int, type: MediaType): List<MediaItem> {
        if (query.isBlank()) return emptyList()
        val apiKey = context.getString(R.string.apiKey)
        return try {
            when (type) {
                MediaType.MOVIE -> movieApiService.searchMovies(query = query, apiKey = apiKey, page = page).results.map { it.toDomain() }
                MediaType.SERIES -> seriesApiService.searchSeries(query = query, apiKey = apiKey, page = page).results.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e("ApiMediaRepository", "Error en searchMedia: ${e.message}")
            emptyList()
        }
    }

    /**
     * Delega la inserción al repositorio local.
     */
    override suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>) {
        roomRepository.cacheMediaItems(mediaItems)
    }

    /**
     * Delega la lectura al repositorio local.
     */
    override fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>> {
        return roomRepository.getCachedMediaItems(type)
    }

    /**
     * Delega la lectura al repositorio local.
     */
    override fun getAllMediaFromLocalDb(): Flow<List<MediaItem>> {
        return roomRepository.getAllCachedMedia()
    }

    /**
     * Implementación de Paging 3 para carga eficiente
     */
    override fun getPagedMedia(
        mediaType: MediaType,
        sortType: HomeViewModel.SortType,
        searchQuery: String?
    ): Flow<PagingData<MediaItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                MediaPagingSource(
                    movieApiService = movieApiService,
                    seriesApiService = seriesApiService,
                    context = context,
                    mediaType = mediaType,
                    sortType = sortType,
                    searchQuery = searchQuery,
                    roomRepository = roomRepository
                )
            }
        ).flow
    }

    // En ApiMediaRepository.kt
    override suspend fun getMovieDetails(movieId: Int): AppResult<MediaDetailItem> {
        return try {
            val response = movieApiService.getMovieDetails(
                movieId = movieId,
                apiKey = context.getString(R.string.apiKey)
            )
            AppResult.Success(response.toDetailedDomain())
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun getSeriesDetails(seriesId: Int): AppResult<MediaDetailItem> {
        return try {
            val response = seriesApiService.getSeriesDetails(
                seriesId = seriesId,
                apiKey = context.getString(R.string.apiKey)
            )
            AppResult.Success(response.toDetailedDomain())
        } catch (e: Exception) {
            AppResult.Error(e)
        }
    }

    override suspend fun hasDetailsCached(id: Int, type: MediaType): Boolean = false
}