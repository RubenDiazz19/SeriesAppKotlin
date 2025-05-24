package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.ProductionCompanyItem
import com.example.peliculasserieskotlin.core.model.ProductionCountryItem
import com.example.peliculasserieskotlin.core.model.SpokenLanguageItem
import com.example.peliculasserieskotlin.core.model.MediaDetailItem

/**
 * Modelo que representa los detalles completos de una película desde la API TMDB.
 */
data class MovieDetailResponse(
    val id: Int?,                   // ID único de la película
    val title: String?,             // Título de la película
    val original_title: String?,    // Título original
    val release_date: String?,      // Fecha de lanzamiento
    val overview: String?,          // Descripción de la película
    val poster_path: String?,       // Ruta al póster
    val backdrop_path: String?,     // Ruta a la imagen de fondo
    val vote_average: Double?,      // Puntuación (0-10)
    val vote_count: Int?,           // Número de votos
    val popularity: Double?,        // Popularidad
    val runtime: Int?,              // Duración en minutos
    val budget: Long?,              // Presupuesto
    val revenue: Long?,             // Ingresos
    val tagline: String?,           // Eslogan
    val status: String?,            // Estado (Released, In Production, etc.)
    val genres: List<Genre>?,       // Géneros
    val production_companies: List<ProductionCompany>?, // Compañías productoras
    val production_countries: List<ProductionCountry>?, // Países de producción
    val spoken_languages: List<SpokenLanguage>?,       // Idiomas hablados
    val imdb_id: String?,           // ID de IMDB
    val homepage: String?,          // Página web oficial
    val adult: Boolean?             // Contenido para adultos
)

/**
 * Modelo para representar un género de película.
 */
data class Genre(
    val id: Int?,
    val name: String?
)

/**
 * Modelo para representar una compañía productora.
 */
data class ProductionCompany(
    val id: Int?,
    val logo_path: String?,
    val name: String?,
    val origin_country: String?
)

/**
 * Modelo para representar un país de producción.
 */
data class ProductionCountry(
    val iso_3166_1: String?,
    val name: String?
)

/**
 * Modelo para representar un idioma hablado en la película.
 */
data class SpokenLanguage(
    val english_name: String?,
    val iso_639_1: String?,
    val name: String?
)

/**
 * Convierte un MovieDetailResponse a un MovieDetailItem del dominio con todos los detalles.
 * Maneja valores nulos con valores predeterminados.
 */
fun MovieDetailResponse.toDetailedDomain(): MediaDetailItem {
    return MediaDetailItem(
        // Campos básicos de MediaItem
        id = id ?: 0,
        title = title ?: "Título desconocido",
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        voteAverage = vote_average ?: 0.0,
        type = MediaType.MOVIE,

        // Campos adicionales
        originalTitle = original_title,
        releaseDate = release_date,
        voteCount = vote_count,
        runtime = runtime,
        budget = budget,
        revenue = revenue,
        tagline = tagline,
        status = status,
        genres = genres?.mapNotNull {
            it.id?.let { genreId -> GenreItem(genreId, it.name ?: "") }
        } ?: emptyList(),
        productionCompanies = production_companies?.mapNotNull {
            ProductionCompanyItem(
                name = it.name ?: "",
                logoPath = it.logo_path,
                originCountry = it.origin_country ?: ""
            )
        } ?: emptyList(),
        productionCountries = production_countries?.mapNotNull {
            it.iso_3166_1?.let { iso -> ProductionCountryItem(iso, it.name ?: "") }
        } ?: emptyList(),
        spokenLanguages = spoken_languages?.mapNotNull {
            it.iso_639_1?.let { iso -> SpokenLanguageItem(it.english_name ?: "", iso, it.name ?: "") }
        } ?: emptyList()
    )
}