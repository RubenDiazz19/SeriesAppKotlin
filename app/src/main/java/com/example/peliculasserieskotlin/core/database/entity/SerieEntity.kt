package com.example.peliculasserieskotlin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.peliculasserieskotlin.core.model.GenreItem
import com.example.peliculasserieskotlin.core.model.Serie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

@Entity(tableName = "series")
data class SerieEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val genres: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

fun SerieEntity.toDomain(): Serie {
    val gson = Gson()
    Log.d("SerieEntity", "toDomain: genres=$genres")
    val genresList = genres?.let {
        val type = object : TypeToken<List<GenreItem>>() {}.type
        try {
            gson.fromJson<List<GenreItem>>(it, type)
        } catch (e: Exception) {
            Log.e("SerieEntity", "Error parseando genres: $it", e)
            null
        }
    }
    Log.d("SerieEntity", "toDomain: genresList=$genresList")
    return Serie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        genres = genresList,
        seasons = emptyList(),
    )
}

fun Serie.toEntity(): SerieEntity {
    val gson = Gson()
    Log.d("SerieEntity", "toEntity: genres=$genres")
    return SerieEntity(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        genres = genres?.let { gson.toJson(it) }
    )
}