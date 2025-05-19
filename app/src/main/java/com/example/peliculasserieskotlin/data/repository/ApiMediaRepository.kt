package com.example.peliculasserieskotlin.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.api.model.toDomain
import com.example.peliculasserieskotlin.data.paging.MediaPagingSource
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class ApiMediaRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val seriesApiService: SeriesApiService,
    @ApplicationContext private val context: Context
) : MediaRepository {

    /**
     * Obtiene medios populares desde la API.
     * @param page Número de página para paginación.
     * @param genre Género (no utilizado en esta implementación).
     * @param type Tipo de medio (MOVIE o SERIES).
     * @return Flow con lista de elementos multimedia.
     */
    override fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>> = flow {
        val apiKey = context.getString(R.string.apiKey) // Obtener apiKey
        val mediaList = when (type) {
            MediaType.MOVIE -> {
                // Nota: El parámetro 'genre' no se usa directamente aquí.
                // El endpoint /popular de TMDB no filtra por género.
                movieApiService.getPopularMovies(apiKey = apiKey, page = page).results.map { it.toDomain() }
            }
            MediaType.SERIES -> {
                // Nota: El parámetro 'genre' no se usa directamente aquí.
                seriesApiService.getPopularSeries(apiKey = apiKey, page = page).results.map { it.toDomain() }
            }
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
        val apiKey = context.getString(R.string.apiKey) // Obtener apiKey
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
        val apiKey = context.getString(R.string.apiKey) // Obtener apiKey
        // Para "Discover", usar los endpoints getAllMovies y getAllSeries
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
            // Registrar el error pero devolver lista vacía para evitar fallos
            Log.e("ApiMediaRepository", "Error en searchMedia: ${e.message}")
            emptyList()
        }
    }

    /**
     * No implementado: Este repositorio no maneja operaciones de base de datos.
     * @throws UnsupportedOperationException Siempre, ya que no es compatible.
     */
    override suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>) {
        // ApiMediaRepository no se encarga de la base de datos local.
        // Esta operación debería ser manejada por RoomMediaRepository.
        throw UnsupportedOperationException("ApiMediaRepository no puede insertar en la base de datos local.")
    }

    /**
     * No implementado: Este repositorio no maneja operaciones de base de datos.
     * @throws UnsupportedOperationException Siempre, ya que no es compatible.
     */
    override fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>> {
        // ApiMediaRepository no se encarga de la base de datos local.
        throw UnsupportedOperationException("ApiMediaRepository no puede leer de la base de datos local.")
    }

    /**
     * No implementado: Este repositorio no maneja operaciones de base de datos.
     * @throws UnsupportedOperationException Siempre, ya que no es compatible.
     */
    override fun getAllMediaFromLocalDb(): Flow<List<MediaItem>> {
        // ApiMediaRepository no se encarga de la base de datos local.
        throw UnsupportedOperationException("ApiMediaRepository no puede leer todos los items de la base de datos local.")
    }

    /**
     * Implementación de Paging 3 para carga eficiente
     */
    override fun getPagedMedia(
        mediaType: MediaType,
        sortType: HomeViewModel.SortType,
        searchQuery: String?
    ): Flow<PagingData<MediaItem>> {
        val apiKey = context.getString(R.string.apiKey)
        
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
                    apiKey = apiKey,
                    mediaType = mediaType,
                    sortType = sortType,
                    searchQuery = searchQuery
                )
            }
        ).flow
    }
}