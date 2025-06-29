package com.example.peliculasserieskotlin.core.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.peliculasserieskotlin.R
import com.example.peliculasserieskotlin.data.MovieApiService
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.data.model.toDomain
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import com.example.peliculasserieskotlin.features.shared.repository.RoomMediaRepository

class MediaPagingSource(
    private val movieApiService: MovieApiService,
    private val seriesApiService: SeriesApiService,
    private val context: Context,
    private val mediaType: MediaType,
    private val sortType: HomeViewModel.SortType,
    private val searchQuery: String? = null,
    private val genreIds: List<Int>? = null,
    private val roomRepository: RoomMediaRepository
) : PagingSource<Int, MediaItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 1
            val apiKey = context.getString(R.string.apiKey)
            
            val response = when {
                !searchQuery.isNullOrBlank() -> {
                    // Búsqueda
                    when (mediaType) {
                        MediaType.MOVIE -> movieApiService.searchMovies(
                            query = searchQuery,
                            apiKey = apiKey,
                            page = page
                        ).results.map { it.toDomain() }
                        MediaType.SERIES -> seriesApiService.searchSeries(
                            query = searchQuery,
                            apiKey = apiKey,
                            page = page
                        ).results.map { it.toDomain() }
                    }
                }
                else -> {
                    // Carga normal según tipo de ordenación
                    when (sortType) {
                        HomeViewModel.SortType.RATING -> {
                            when (mediaType) {
                                MediaType.MOVIE -> movieApiService.getTopRatedMovies(
                                    apiKey = apiKey,
                                    page = page
                                ).results.map { it.toDomain() }
                                MediaType.SERIES -> seriesApiService.getTopRatedSeries(
                                    apiKey = apiKey,
                                    page = page
                                ).results.map { it.toDomain() }
                            }
                        }
                        HomeViewModel.SortType.ALPHABETIC -> {
                            when (mediaType) {
                                MediaType.MOVIE -> movieApiService.getPopularMovies(
                                    apiKey = apiKey,
                                    page = page
                                ).results.map { it.toDomain() }
                                MediaType.SERIES -> seriesApiService.getPopularSeries(
                                    apiKey = apiKey,
                                    page = page
                                ).results.map { it.toDomain() }
                            }
                        }
                        HomeViewModel.SortType.FAVORITE -> {
                            // Los favoritos no usan paginación de API
                            emptyList()
                        }
                    }
                }
            }

            // Filtrar por géneros si genreIds no está vacío
            val filteredResponse = if (!genreIds.isNullOrEmpty()) {
                response.filter { item ->
                    val itemGenreIds = item.genres?.map { it.id } ?: emptyList()
                    genreIds.any { it in itemGenreIds }
                }
            } else {
                response
            }

            // Guardar los elementos en caché
            if (filteredResponse.isNotEmpty()) {
                roomRepository.cacheMediaItems(filteredResponse)
            }

            LoadResult.Page(
                data = filteredResponse,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (filteredResponse.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MediaItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}