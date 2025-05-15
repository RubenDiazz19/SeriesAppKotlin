package com.example.peliculasserieskotlin.data.api.model 
  
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
  
/**
 * Modelo que representa una película desde la API.
 * Mapea la respuesta JSON de películas.
 */
data class MovieApiModel( 
    val id: Int?,                // ID único de la película
    val title: String?,          // Título de la película
    val release_date: String?,   // Fecha de lanzamiento
    val overview: String?,       // Descripción de la película
    val poster_path: String?,    // Ruta al póster
    val backdrop_path: String?,  // Ruta a la imagen de fondo
    val vote_average: Double?    // Puntuación (0-10)
) 
  
/**
 * Convierte un MovieApiModel a un MediaItem del dominio.
 * Maneja valores nulos con valores predeterminados.
 */
fun MovieApiModel.toDomain(): MediaItem {
    return MediaItem(
        id = id ?: 0,
        title = title ?: "Título desconocido",
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        voteAverage = vote_average ?: 0.0,
        type = MediaType.MOVIE
    ) 
} 
  
/**
 * Respuesta paginada de la API de películas.
 */
data class MovieApiResponse( 
    val results: List<MovieApiModel>  // Lista de películas
)
