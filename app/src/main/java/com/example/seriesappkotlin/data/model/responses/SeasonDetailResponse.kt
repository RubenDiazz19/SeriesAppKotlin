package com.example.seriesappkotlin.data.model.responses

import com.example.seriesappkotlin.core.model.Episode
import com.example.seriesappkotlin.core.model.Season
import com.example.seriesappkotlin.data.model.MediaConstants
import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa los detalles completos de una temporada desde la API TMDB.
 */
data class SeasonDetailResponse(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("overview")
    val overview: String?,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("season_number")
    val seasonNumber: Int?,
    
    @SerializedName("air_date")
    val airDate: String?,
    
    @SerializedName("episodes")
    val episodes: List<EpisodeResponse>?
) {
    /**
     * Convierte la respuesta de la API a un objeto de dominio Season.
     */
    fun toDomain(): Season {
        return Season(
            id = id ?: 0,
            seasonNumber = seasonNumber ?: 0,
            name = name ?: "Temporada desconocida",
            overview = overview ?: "Sin descripción",
            episodeCount = episodes?.size ?: 0,
            posterUrl = MediaConstants.formatImageUrl(posterPath),
            episodes = episodes?.map { it.toDomain() } ?: emptyList()
        )
    }
}

/**
 * Modelo que representa un episodio desde la API TMDB.
 */
data class EpisodeResponse(
    @SerializedName("id")
    val id: Int?,
    
    @SerializedName("episode_number")
    val episodeNumber: Int?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("overview")
    val overview: String?,
    
    @SerializedName("still_path")
    val stillPath: String?
) {
    /**
     * Convierte la respuesta de la API a un objeto de dominio Episode.
     */
    fun toDomain(): Episode {
        return Episode(
            id = id ?: 0,
            episodeNumber = episodeNumber ?: 0,
            name = name ?: "Episodio desconocido",
            overview = overview ?: "Sin descripción",
            stillPath = MediaConstants.formatImageUrl(stillPath)
        )
    }
}