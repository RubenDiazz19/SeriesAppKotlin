package com.example.peliculasserieskotlin.data.api.model 
  
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
  
data class MovieApiModel( 
    val id: Int?, 
    val title: String?, 
    val release_date: String?, 
    val overview: String?, 
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double? 
) 
  
fun MovieApiModel.toDomain(): MediaItem {

    return MediaItem(
        id = id ?: 0, 
        title = title ?: "Título desconocido", 
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "" ,
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        voteAverage = vote_average ?: 0.0, 
        type = MediaType.MOVIE
    ) 
} 
  
data class MovieApiResponse( 
    val results: List<MovieApiModel> 
)
