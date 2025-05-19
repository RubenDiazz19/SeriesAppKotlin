package com.example.peliculasserieskotlin.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.peliculasserieskotlin.data.api.MovieApiService
import com.example.peliculasserieskotlin.data.api.SeriesApiService
import com.example.peliculasserieskotlin.data.api.model.toDomain
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
import com.example.peliculasserieskotlin.presentation.home.HomeViewModel

class MediaPagingSource(
    private val movieApiService: MovieApiService,
    private val seriesApiService: SeriesApiService,
    private val apiKey: String,
    private val mediaType: MediaType,
    private val sortType: HomeViewModel.SortType,
    private val searchQuery: String? = null
) : PagingSource<Int, MediaItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaItem> {
        return try {
            val page = params.key ?: 1
            
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

            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
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