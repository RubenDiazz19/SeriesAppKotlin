package com.example.peliculasserieskotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.peliculasserieskotlin.core.model.MediaDetailItem
import com.example.peliculasserieskotlin.core.model.MediaType
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.ProductionCompanyItem
import com.example.peliculasserieskotlin.core.model.ProductionCountryItem
import com.example.peliculasserieskotlin.core.model.SpokenLanguageItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Entidad que representa los detalles de un elemento multimedia en la base de datos.
 */
@Entity(tableName = "media_details")
data class MediaDetailEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val mediaType: String,
    val originalTitle: String?,
    val releaseDate: String?,
    val voteCount: Int?,
    val runtime: Int?,
    val budget: Long?,
    val revenue: Long?,
    val genres: String?, // JSON string
    val productionCompanies: String?, // JSON string
    val productionCountries: String?, // JSON string
    val spokenLanguages: String?, // JSON string
    val status: String?,
    val tagline: String?,
    val numberOfSeasons: Int?, // Solo para series
    val numberOfEpisodes: Int?, // Solo para series
    val firstAirDate: String?, // Solo para series
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Convierte una entidad de detalles a objeto de dominio.
 */
fun MediaDetailEntity.toDomain(): MediaDetailItem {
    val gson = Gson()
    
    val genresList = genres?.let {
        val type = object : TypeToken<List<GenreItem>>() {}.type
        gson.fromJson<List<GenreItem>>(it, type)
    }
    
    val productionCompaniesList = productionCompanies?.let {
        val type = object : TypeToken<List<ProductionCompanyItem>>() {}.type
        gson.fromJson<List<ProductionCompanyItem>>(it, type)
    }
    
    val productionCountriesList = productionCountries?.let {
        val type = object : TypeToken<List<ProductionCountryItem>>() {}.type
        gson.fromJson<List<ProductionCountryItem>>(it, type)
    }
    
    val spokenLanguagesList = spokenLanguages?.let {
        val type = object : TypeToken<List<SpokenLanguageItem>>() {}.type
        gson.fromJson<List<SpokenLanguageItem>>(it, type)
    }

    return when (MediaType.valueOf(mediaType)) {
        MediaType.MOVIE -> MediaDetailItem.MovieDetailItem(
            id = id,
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            voteAverage = voteAverage,
            type = MediaType.MOVIE,
            originalTitle = originalTitle,
            releaseDate = releaseDate,
            voteCount = voteCount,
            runtime = runtime,
            budget = budget,
            revenue = revenue,
            genres = genresList,
            productionCompanies = productionCompaniesList,
            productionCountries = productionCountriesList,
            spokenLanguages = spokenLanguagesList,
            status = status,
            tagline = tagline
        )
        MediaType.SERIES -> MediaDetailItem.SeriesDetailItem(
            id = id,
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            voteAverage = voteAverage,
            type = MediaType.SERIES,
            originalTitle = originalTitle,
            firstAirDate = firstAirDate,
            voteCount = voteCount,
            runtime = runtime,
            numberOfSeasons = numberOfSeasons,
            numberOfEpisodes = numberOfEpisodes,
            genres = genresList,
            productionCompanies = productionCompaniesList,
            productionCountries = productionCountriesList,
            spokenLanguages = spokenLanguagesList,
            status = status,
            tagline = tagline
        )
    }
}

/**
 * Convierte un objeto de dominio de detalles a entidad.
 */
fun MediaDetailItem.toEntity(): MediaDetailEntity {
    val gson = Gson()
    
    return when (this) {
        is MediaDetailItem.MovieDetailItem -> MediaDetailEntity(
            id = id,
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            voteAverage = voteAverage,
            mediaType = type.name,
            originalTitle = originalTitle,
            releaseDate = releaseDate,
            voteCount = voteCount,
            runtime = runtime,
            budget = budget,
            revenue = revenue,
            genres = genres?.let { gson.toJson(it) },
            productionCompanies = productionCompanies?.let { gson.toJson(it) },
            productionCountries = productionCountries?.let { gson.toJson(it) },
            spokenLanguages = spokenLanguages?.let { gson.toJson(it) },
            status = status,
            tagline = tagline,
            numberOfSeasons = null,
            numberOfEpisodes = null,
            firstAirDate = null
        )
        is MediaDetailItem.SeriesDetailItem -> MediaDetailEntity(
            id = id,
            title = title,
            overview = overview,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            voteAverage = voteAverage,
            mediaType = type.name,
            originalTitle = originalTitle,
            releaseDate = null,
            voteCount = voteCount,
            runtime = runtime,
            budget = null,
            revenue = null,
            genres = genres?.let { gson.toJson(it) },
            productionCompanies = productionCompanies?.let { gson.toJson(it) },
            productionCountries = productionCountries?.let { gson.toJson(it) },
            spokenLanguages = spokenLanguages?.let { gson.toJson(it) },
            status = status,
            tagline = tagline,
            numberOfSeasons = numberOfSeasons,
            numberOfEpisodes = numberOfEpisodes,
            firstAirDate = firstAirDate
        )
    }
} 