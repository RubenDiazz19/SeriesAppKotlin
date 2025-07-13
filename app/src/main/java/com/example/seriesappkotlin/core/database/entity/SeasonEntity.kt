package com.example.seriesappkotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.seriesappkotlin.core.model.Episode
import com.example.seriesappkotlin.core.model.Season
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "seasons")
data class SeasonEntity(
    @PrimaryKey
    val id: Int,
    val serieId: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val episodeCount: Int,
    val posterUrl: String?,
    val episodes: String, // JSON string de episodios
    val timestamp: Long = System.currentTimeMillis()
)

// Extensiones para conversión
fun Season.toEntity(serieId: Int): SeasonEntity {
    val gson = Gson()
    return SeasonEntity(
        id = id,
        serieId = serieId,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        episodeCount = episodeCount,
        posterUrl = posterUrl,
        episodes = gson.toJson(episodes)
    )
}

fun SeasonEntity.toDomain(): Season {
    val gson = Gson()
    val episodeListType = object : TypeToken<List<Episode>>() {}.type
    val episodeList: List<Episode> = try {
        gson.fromJson(episodes, episodeListType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    
    return Season(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        episodeCount = episodeCount,
        posterUrl = posterUrl,
        episodes = episodeList
    )
}