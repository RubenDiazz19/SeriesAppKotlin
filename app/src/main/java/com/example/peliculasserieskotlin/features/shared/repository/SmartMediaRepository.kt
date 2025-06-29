package com.example.peliculasserieskotlin.features.shared.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.peliculasserieskotlin.core.database.entity.toDomain
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.paging.MediaPagingSource
import com.example.peliculasserieskotlin.core.util.NetworkUtils
import com.example.peliculasserieskotlin.core.util.AppResult
import com.example.peliculasserieskotlin.data.MovieApiService
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartMediaRepository @Inject constructor(
    private val apiRepository: ApiMediaRepository,
    private val roomRepository: RoomMediaRepository,
    private val networkUtils: NetworkUtils,
    private val movieApiService: MovieApiService,
    private val seriesApiService: SeriesApiService,
    @ApplicationContext private val context: Context
) : MediaRepository {

    companion object {
        private const val TAG = "SmartMediaRepository"
        private const val CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 horas en milisegundos
    }

    // ==================== MÉTODOS PARA LISTAS DE MEDIA ====================

    override fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>> = flow {
        val cachedFlow = roomRepository.getCachedMediaItems(type)
        val cachedData = cachedFlow.first()

        if (cachedData.isNotEmpty()) {
            emit(cachedData)
        }

        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getPopularMedia(page, genre, type).first()
                if (freshData.isNotEmpty()) {
                    roomRepository.cacheMediaItems(freshData)
                    emit(freshData)
                } else {
                    emit(cachedData)
                }
            } catch (e: Exception) {
                emit(cachedData)
            }
        } else {
            // Si no hay internet, observa el flujo local para ese tipo
            emitAll(cachedFlow)
        }
    }

    override fun getTopRatedMedia(page: Int, type: MediaType): Flow<List<MediaItem>> = flow {
        // Primero intentar obtener del caché
        val cachedData = roomRepository.getCachedMediaItems(type).first()
        
        if (cachedData.isNotEmpty()) {
            Log.d(TAG, "Emitting cached top rated media for $type")
            emit(cachedData)
        }
        
        // Si hay internet, intentar obtener datos frescos
        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getTopRatedMedia(page, type).first()
                if (freshData.isNotEmpty()) {
                    // Guardar en caché
                    roomRepository.cacheMediaItems(freshData)
                    Log.d(TAG, "Updated cache with fresh top rated media for $type")
                    emit(freshData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching fresh top rated media: ${e.message}")
                if (cachedData.isEmpty()) {
                    throw e
                }
            }
        } else {
            Log.d(TAG, "No network available, using cached data for $type")
            if (cachedData.isEmpty()) {
                throw Exception("No hay datos en caché y no hay conexión a internet")
            }
        }
    }

    override fun getDiscoverMedia(page: Int, type: MediaType): Flow<List<MediaItem>> = flow {
        // Primero intentar obtener del caché
        val cachedData = roomRepository.getCachedMediaItems(type).first()
        
        if (cachedData.isNotEmpty()) {
            Log.d(TAG, "Emitting cached discover media for $type")
            emit(cachedData)
        }
        
        // Si hay internet, intentar obtener datos frescos
        if (networkUtils.isNetworkAvailable()) {
            try {
                val freshData = apiRepository.getDiscoverMedia(page, type).first()
                if (freshData.isNotEmpty()) {
                    // Guardar en caché
                    roomRepository.cacheMediaItems(freshData)
                    Log.d(TAG, "Updated cache with fresh discover media for $type")
                    emit(freshData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching fresh discover media: ${e.message}")
                if (cachedData.isEmpty()) {
                    throw e
                }
            }
        } else {
            Log.d(TAG, "No network available, using cached data for $type")
            if (cachedData.isEmpty()) {
                throw Exception("No hay datos en caché y no hay conexión a internet")
            }
        }
    }

    override suspend fun searchMedia(query: String, page: Int, type: MediaType): List<MediaItem> {
        if (query.isBlank()) return emptyList()

        // Primero buscar en caché
        val cachedResults = roomRepository.searchCachedMediaItems(type, query)
        if (cachedResults.isNotEmpty()) {
            Log.d(TAG, "Found cached search results for '$query'")
            return cachedResults
        }

        // Si no hay resultados en caché y hay internet, buscar en API
        if (networkUtils.isNetworkAvailable()) {
            try {
                val apiResults = apiRepository.searchMedia(query, page, type)
                if (apiResults.isNotEmpty()) {
                    // Guardar resultados en caché
                    roomRepository.cacheMediaItems(apiResults)
                    Log.d(TAG, "Cached search results for '$query'")
                }
                return apiResults
            } catch (e: Exception) {
                Log.e(TAG, "Error searching media: ${e.message}")
                throw e
            }
        } else {
            Log.d(TAG, "No network available for search, returning cached results")
            return cachedResults
        }
    }

    // ==================== MÉTODOS PARA DETALLES ====================

    override suspend fun getMovieDetails(movieId: Int): AppResult<MediaDetailItem> {
        // Primero verificar si está en caché
        val cachedDetail = roomRepository.getCachedDetail(movieId, MediaType.MOVIE)
        if (cachedDetail != null && !isCacheExpired(cachedDetail.lastUpdated)) {
            Log.d(TAG, "Returning cached movie details for ID: $movieId")
            return AppResult.Success(cachedDetail.toDomain())
        }

        // Si no está en caché o está expirado y hay internet, obtener de API
        if (networkUtils.isNetworkAvailable()) {
            try {
                val apiResult = apiRepository.getMovieDetails(movieId)
                if (apiResult is AppResult.Success) {
                    // Guardar en caché
                    roomRepository.cacheDetail(apiResult.data)
                    Log.d(TAG, "Cached movie details for ID: $movieId")
                }
                return apiResult
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching movie details: ${e.message}")
                // Si falla la API pero tenemos caché (aunque esté expirado), devolverlo
                if (cachedDetail != null) {
                    Log.d(TAG, "API failed, returning expired cached movie details for ID: $movieId")
                    return AppResult.Success(cachedDetail.toDomain())
                }
                return AppResult.Error(e)
            }
        } else {
            // Sin internet, devolver caché si existe (aunque esté expirado)
            if (cachedDetail != null) {
                Log.d(TAG, "No network, returning cached movie details for ID: $movieId")
                return AppResult.Success(cachedDetail.toDomain())
            }
            return AppResult.Error(Exception("No hay datos en caché y no hay conexión a internet"))
        }
    }

    override suspend fun getSeriesDetails(seriesId: Int): AppResult<MediaDetailItem> {
        // Primero verificar si está en caché
        val cachedDetail = roomRepository.getCachedDetail(seriesId, MediaType.SERIES)
        if (cachedDetail != null && !isCacheExpired(cachedDetail.lastUpdated)) {
            Log.d(TAG, "Returning cached series details for ID: $seriesId")
            return AppResult.Success(cachedDetail.toDomain())
        }

        // Si no está en caché o está expirado y hay internet, obtener de API
        if (networkUtils.isNetworkAvailable()) {
            try {
                val apiResult = apiRepository.getSeriesDetails(seriesId)
                if (apiResult is AppResult.Success) {
                    // Guardar en caché
                    roomRepository.cacheDetail(apiResult.data)
                    Log.d(TAG, "Cached series details for ID: $seriesId")
                }
                return apiResult
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching series details: ${e.message}")
                // Si falla la API pero tenemos caché (aunque esté expirado), devolverlo
                if (cachedDetail != null) {
                    Log.d(TAG, "API failed, returning expired cached series details for ID: $seriesId")
                    return AppResult.Success(cachedDetail.toDomain())
                }
                return AppResult.Error(e)
            }
        } else {
            // Sin internet, devolver caché si existe (aunque esté expirado)
            if (cachedDetail != null) {
                Log.d(TAG, "No network, returning cached series details for ID: $seriesId")
                return AppResult.Success(cachedDetail.toDomain())
            }
            return AppResult.Error(Exception("No hay datos en caché y no hay conexión a internet"))
        }
    }

    // ==================== MÉTODOS DE PAGING ====================

    override fun getPagedMedia(
        mediaType: MediaType,
        sortType: HomeViewModel.SortType,
        searchQuery: String?,
        genreIds: List<Int>?
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
                    genreIds = genreIds,
                    roomRepository = roomRepository
                )
            }
        ).flow
    }

    // ==================== MÉTODOS DE BASE DE DATOS LOCAL ====================

    override suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>) {
        roomRepository.cacheMediaItems(mediaItems)
    }

    override fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>> {
        return roomRepository.getCachedMediaItems(type)
    }

    override fun getAllMediaFromLocalDb(): Flow<List<MediaItem>> {
        return roomRepository.getAllCachedMedia()
    }

    override suspend fun hasDetailsCached(id: Int, type: MediaType): Boolean {
        return roomRepository.hasDetailCached(id, type)
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private fun isCacheExpired(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > CACHE_DURATION
    }
} 