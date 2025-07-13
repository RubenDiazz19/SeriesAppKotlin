package com.example.peliculasserieskotlin.core.paging

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.peliculasserieskotlin.data.SeriesApiService
import com.example.peliculasserieskotlin.data.model.toDomain
import com.example.peliculasserieskotlin.core.model.Serie
import com.example.peliculasserieskotlin.features.home.HomeViewModel
import com.example.peliculasserieskotlin.features.shared.repository.RoomSerieRepository
import com.example.seriesappkotlin.R

class SeriesPagingSource(
    private val seriesApiService: SeriesApiService,
    private val context: Context,
    private val sortType: HomeViewModel.SortType,
    private val searchQuery: String? = null,
    private val genreIds: List<Int>? = null,
    private val roomRepository: RoomSerieRepository
) : PagingSource<Int, Serie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Serie> {
        return try {
            val page = params.key ?: 1
            val apiKey = context.getString(R.string.apiKey)

            val response = when {
                !searchQuery.isNullOrBlank() -> {
                    seriesApiService.searchSeries(
                        query = searchQuery,
                        apiKey = apiKey,
                        page = page
                    ).results.map { it.toDomain() }
                }

                else -> {
                    when (sortType) {
                        HomeViewModel.SortType.RATING -> {
                            seriesApiService.getTopRatedSeries(
                                apiKey = apiKey,
                                page = page
                            ).results.map { it.toDomain() }
                        }

                        HomeViewModel.SortType.POPULAR -> {
                            seriesApiService.getPopularSeries(
                                apiKey = apiKey,
                                page = page
                            ).results.map { it.toDomain() }
                        }

                        HomeViewModel.SortType.FAVORITE -> {
                            emptyList()
                        }
                    }
                }
            }

            val filteredResponse = if (!genreIds.isNullOrEmpty()) {
                response.filter { item ->
                    val itemGenreIds = item.genres?.map { it.id } ?: emptyList()
                    genreIds.any { it in itemGenreIds }
                }
            } else {
                response
            }

            if (filteredResponse.isNotEmpty()) {
                roomRepository.cacheSeries(filteredResponse)
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

    override fun getRefreshKey(state: PagingState<Int, Serie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}