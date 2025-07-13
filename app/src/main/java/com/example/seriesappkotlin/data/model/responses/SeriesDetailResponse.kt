package com.example.seriesappkotlin.data.model.responses

import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.model.SerieDetailItem
import com.example.seriesappkotlin.data.model.MediaConstants
import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa los detalles completos de una serie desde la API TMDB.
 */
data class SeriesDetailResponse(
    @SerializedName("id")
    val id: Int?,                   // ID único de la serie
    
    @SerializedName("name")
    val name: String?,              // Nombre de la serie
    
    @SerializedName("original_name")
    val originalName: String?,      // Nombre original
    
    @SerializedName("first_air_date")
    val firstAirDate: String?,      // Fecha de primera emisión
    
    @SerializedName("overview")
    val overview: String?,          // Descripción de la serie
    
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
    
    @SerializedName("episode_run_time")
    val episodeRunTime: List<Int>?, // Duración de episodios en minutos
    
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int?,     // Número de episodios
    
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int?,      // Número de temporadas
    
    @SerializedName("tagline")
    val tagline: String?,           // Eslogan
    
    @SerializedName("status")
    val status: String?,            // Estado (En emisión, Finalizada, etc.)
    
    @SerializedName("genres")
    val genres: List<Genre>?,       // Géneros
    
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>?, // Compañías productoras
    
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>?, // Países de producción
    
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?, // Idiomas hablados
    
    @SerializedName("networks")
    val networks: List<NetworkResponse>? // Redes de televisión
) {
    /**
     * Valida que los campos críticos no sean nulos.
     * @return true si los campos críticos son válidos
     */
    fun isValid(): Boolean {
        return id != null && name != null
    }

    /**
     * Convierte un SeriesDetailResponse a un MediaDetailItem del dominio con todos los detalles.
     * Maneja valores nulos con valores predeterminados.
     */
    fun toDetailedDomain(): SerieDetailItem.SeriesDetailItem {
        require(isValid()) { "SeriesDetailResponse no es válido: id o name son nulos" }
        
        return SerieDetailItem.SeriesDetailItem(
            id = id ?: 0,
            title = name ?: "Serie desconocida",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            originalTitle = originalName,
            firstAirDate = firstAirDate,
            voteCount = voteCount,
            runtime = episodeRunTime?.firstOrNull(),
            numberOfSeasons = numberOfSeasons,
            numberOfEpisodes = numberOfEpisodes,
            genres = genres?.mapNotNull { it.id?.let { genreId -> GenreItem(genreId, it.name ?: "") } },
            status = status,
            tagline = tagline
        )
    }

    /**
     * Convierte SeriesDetailResponse a Serie del dominio.
     */
    fun toDomain(): Serie {
        require(isValid()) { "SeriesDetailResponse no es válido: id o name son nulos" }
        
        return Serie(
            id = id ?: 0,
            title = name ?: "Serie desconocida",
            overview = overview ?: "Sin descripción",
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            backdropUrl = MediaConstants.formatImageUrl(backdropPath, MediaConstants.DEFAULT_BACKDROP_SIZE),
            voteAverage = voteAverage ?: 0.0,
            originalTitle = originalName,
            firstAirDate = firstAirDate,
            voteCount = voteCount,
            runtime = episodeRunTime?.firstOrNull(),
            numberOfSeasons = numberOfSeasons,
            numberOfEpisodes = numberOfEpisodes,
            genres = genres?.mapNotNull { it.id?.let { genreId -> GenreItem(genreId, it.name ?: "") } },
            status = status,
            tagline = tagline
        )
    }
}

/**
 * Modelo para representar una red de televisión
 */
data class NetworkResponse(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("logo_path")
    val logoPath: String?
)

/**
 * Modelo para representar un género desde la API
 */
data class Genre(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)

/**
 * Modelo para representar una compañía productora desde la API
 */
data class ProductionCompany(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("origin_country")
    val originCountry: String?
)

/**
 * Modelo para representar un país de producción desde la API
 */
data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso3166_1: String?,
    @SerializedName("name")
    val name: String?
)

/**
 * Modelo para representar un idioma hablado desde la API
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
fun SeriesDetailResponse.toDetailedDomain(): SerieDetailItem.SeriesDetailItem = this.toDetailedDomain()

