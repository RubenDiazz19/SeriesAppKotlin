package com.example.peliculasserieskotlin.data.model.responses

import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.ProductionCompanyItem
import com.example.peliculasserieskotlin.core.model.ProductionCountryItem
import com.example.peliculasserieskotlin.core.model.SpokenLanguageItem
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.data.model.MediaConstants
import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa los detalles completos de una película desde la API TMDB.
 */
data class MovieDetailResponse(
    @SerializedName("id")
    val id: Int?,                   // ID único de la película
    
    @SerializedName("title")
    val title: String?,             // Título de la película
    
    @SerializedName("original_title")
    val originalTitle: String?,     // Título original
    
    @SerializedName("release_date")
    val releaseDate: String?,       // Fecha de lanzamiento
    
    @SerializedName("overview")
    val overview: String?,          // Descripción de la película
    
    @SerializedName("poster_path")
    val posterPath: String?,        // Ruta al póster
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,      // Ruta a la imagen de fondo
    
    @SerializedName("vote_average")
    val voteAverage: Double?,       // Puntuación (0-10)
    
    @SerializedName("vote_count")
    val voteCount: Int?,            // Número de votos
    
    @SerializedName("popularity")
    val popularity: Double?,        // Popularidad
    
    @SerializedName("runtime")
    val runtime: Int?,              // Duración en minutos
    
    @SerializedName("budget")
    val budget: Long?,              // Presupuesto
    
    @SerializedName("revenue")
    val revenue: Long?,             // Ingresos
    
    @SerializedName("tagline")
    val tagline: String?,           // Eslogan
    
    @SerializedName("status")
    val status: String?,            // Estado (Released, In Production, etc.)
    
    @SerializedName("genres")
    val genres: List<Genre>?,       // Géneros
    
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>?, // Compañías productoras
    
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>?, // Países de producción
    
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?,       // Idiomas hablados
    
    @SerializedName("imdb_id")
    val imdbId: String?,            // ID de IMDB
    
    @SerializedName("homepage")
    val homepage: String?,          // Página web oficial
    
    @SerializedName("adult")
    val adult: Boolean?             // Contenido para adultos
) {
    /**
     * Valida que los campos críticos no sean nulos.
     * @return true si los campos críticos son válidos
     */
    fun isValid(): Boolean {
        return id != null && title != null
    }

    /**
     * Convierte un MovieDetailResponse a un MediaDetailItem del dominio con todos los detalles.
     * Maneja valores nulos con valores predeterminados.
     */
    fun toDetailedDomain(): MediaDetailItem.MovieDetailItem {
        require(isValid()) { "MovieDetailResponse no es válido: id o title son nulos" }
        
        return MediaDetailItem.MovieDetailItem(
            id = id ?: 0,
            title = title ?: "Título desconocido",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            originalTitle = originalTitle,
            releaseDate = releaseDate,
            voteCount = voteCount,
            runtime = runtime,
            budget = budget,
            revenue = revenue,
            genres = genres?.mapNotNull { it.id?.let { genreId -> GenreItem(genreId, it.name ?: "") } },
            productionCompanies = productionCompanies?.mapNotNull {
                ProductionCompanyItem(
                    name = it.name ?: "",
                    logoPath = MediaConstants.formatImageUrl(it.logoPath),
                    originCountry = it.originCountry ?: ""
                )
            },
            productionCountries = productionCountries?.mapNotNull {
                it.iso3166_1?.let { iso -> ProductionCountryItem(iso, it.name ?: "") }
            },
            spokenLanguages = spokenLanguages?.mapNotNull {
                it.iso639_1?.let { iso -> SpokenLanguageItem(it.englishName ?: "", iso, it.name ?: "") }
            },
            status = status,
            tagline = tagline,
            type = MediaType.MOVIE
        )
    }
}

/**
 * Modelo para representar un género de película.
 */
data class Genre(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("name")
    val name: String?
)

/**
 * Modelo para representar una compañía productora.
 */
data class ProductionCompany(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("logo_path")
    val logoPath: String?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("origin_country")
    val originCountry: String?
)

/**
 * Modelo para representar un país de producción.
 */
data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso3166_1: String?,
    
    @SerializedName("name")
    val name: String?
)

/**
 * Modelo para representar un idioma hablado en la película.
 */
data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String?,
    
    @SerializedName("iso_639_1")
    val iso639_1: String?,
    
    @SerializedName("name")
    val name: String?
)

// Función de extensión para compatibilidad
fun MovieDetailResponse.toDetailedDomain(): MediaDetailItem.MovieDetailItem = this.toDetailedDomain()