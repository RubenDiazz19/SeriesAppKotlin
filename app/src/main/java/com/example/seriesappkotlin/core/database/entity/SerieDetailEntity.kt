package com.example.seriesappkotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.seriesappkotlin.core.model.GenreItem
import com.example.seriesappkotlin.core.model.Serie
import com.example.seriesappkotlin.core.model.Season
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

@Entity(tableName = "series_details")
data class SerieDetailEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val voteCount: Int?,
    val popularity: Double?,
    val firstAirDate: String?,
    val numberOfEpisodes: Int?,
    val numberOfSeasons: Int?,
    val episodeRunTime: String?, // JSON string de List<Int>
    val tagline: String?,
    val status: String?,
    val genres: String?, // JSON string de List<GenreItem>
    val seasons: String?, // JSON string de List<Season>
    val productionCompanies: String?, // JSON string de List<ProductionCompanyItem>
    val productionCountries: String?, // JSON string de List<ProductionCountryItem>
    val spokenLanguages: String?, // JSON string de List<SpokenLanguageItem>
    val networks: String? // JSON string de List<NetworkItem>
)

fun SerieDetailEntity.toDomain(): Serie {
    val gson = Gson()
    Log.d("SerieDetailEntity", "toDomain: genres=$genres, seasons=$seasons")
    val genresList = genres?.let {
        val type = object : TypeToken<List<GenreItem>>() {}.type
        try {
            gson.fromJson<List<GenreItem>>(it, type)
        } catch (e: Exception) {
            Log.e("SerieDetailEntity", "Error parseando genres: $it", e)
            null
        }
    }
    val seasonsList = seasons?.let {
        val type = object : TypeToken<List<Season>>() {}.type
        try {
            gson.fromJson<List<Season>>(it, type)
        } catch (e: Exception) {
            Log.e("SerieDetailEntity", "Error parseando seasons: $it", e)
            null
        }
    } ?: emptyList()
    Log.d("SerieDetailEntity", "toDomain: genresList=$genresList, seasonsList=$seasonsList")
    return Serie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        genres = genresList,
        seasons = seasonsList
    )
}

fun Serie.toDetailEntity(
    originalTitle: String? = null,
    voteCount: Int? = null,
    popularity: Double? = null,
    firstAirDate: String? = null,
    numberOfEpisodes: Int? = null,
    numberOfSeasons: Int? = null,
    episodeRunTime: List<Int>? = null,
    tagline: String? = null,
    status: String? = null,
    productionCompanies: List<Any>? = null,
    productionCountries: List<Any>? = null,
    spokenLanguages: List<Any>? = null,
    networks: List<Any>? = null
): SerieDetailEntity {
    val gson = Gson()
    Log.d("SerieDetailEntity", "toDetailEntity: genres=$genres, seasons=$seasons")
    return SerieDetailEntity(
        id = id,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        firstAirDate = firstAirDate,
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        episodeRunTime = episodeRunTime?.let { gson.toJson(it) },
        tagline = tagline,
        status = status,
        genres = genres?.let { gson.toJson(it) },
        seasons = if (seasons.isNotEmpty()) gson.toJson(seasons) else null,
        productionCompanies = productionCompanies?.let { gson.toJson(it) },
        productionCountries = productionCountries?.let { gson.toJson(it) },
        spokenLanguages = spokenLanguages?.let { gson.toJson(it) },
        networks = networks?.let { gson.toJson(it) }
    )
}