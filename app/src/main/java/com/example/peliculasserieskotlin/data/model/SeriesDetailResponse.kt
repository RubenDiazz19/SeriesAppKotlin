package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.ProductionCompanyItem
import com.example.peliculasserieskotlin.core.model.ProductionCountryItem
import com.example.peliculasserieskotlin.core.model.SpokenLanguageItem
import com.example.peliculasserieskotlin.core.model.MediaDetailItem

/**
 * Modelo que representa los detalles completos de una serie desde la API TMDB.
 */
data class SeriesDetailResponse(
    val id: Int?,                   // ID único de la serie
    val name: String?,              // Nombre de la serie
    val original_name: String?,     // Nombre original
    val first_air_date: String?,    // Fecha de primera emisión
    val overview: String?,          // Descripción de la serie
    val poster_path: String?,       // Ruta al póster
    val backdrop_path: String?,     // Ruta a la imagen de fondo
    val vote_average: Double?,      // Puntuación (0-10)
    val vote_count: Int?,           // Número de votos
    val popularity: Double?,        // Popularidad
    val episode_run_time: List<Int>?, // Duración de episodios en minutos
    val number_of_episodes: Int?,   // Número de episodios
    val number_of_seasons: Int?,    // Número de temporadas
    val tagline: String?,           // Eslogan
    val status: String?,            // Estado (En emisión, Finalizada, etc.)
    val genres: List<Genre>?, // Géneros
    val production_companies: List<ProductionCompany>?, // Compañías productoras
    val production_countries: List<ProductionCountry>?, // Países de producción
    val spoken_languages: List<SpokenLanguage>?, // Idiomas hablados
    val networks: List<NetworkResponse>? // Redes de televisión
)

/**
 * Modelo para representar una red de televisión
 */
data class NetworkResponse(
    val id: Int?,
    val name: String?,
    val logo_path: String?
)

/**
 * Extensión para convertir SeriesDetailResponse a MediaDetailItem
 */
fun SeriesDetailResponse.toDetailedDomain(): MediaDetailItem {
    return MediaDetailItem(
        // Campos básicos de MediaItem
        id = id ?: 0,
        title = name ?: "Serie desconocida",
        overview = overview ?: "Sin descripción",
        posterUrl = poster_path?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        backdropUrl = backdrop_path?.let { "https://image.tmdb.org/t/p/w500$it" },
        voteAverage = vote_average ?: 0.0,
        type = MediaType.SERIES,

        // Campos adicionales
        originalTitle = original_name,
        releaseDate = first_air_date,
        voteCount = vote_count,
        runtime = episode_run_time?.firstOrNull(),
        budget = null, // Las series no tienen presupuesto como las películas
        revenue = null, // Las series no tienen ingresos como las películas
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