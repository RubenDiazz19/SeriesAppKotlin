package com.example.peliculasserieskotlin.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType

@Entity(
    tableName = "media_items",
    indices = [Index(value = ["title"]), Index(value = ["voteAverage"])]
)
data class MediaItemEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String?,
    val voteAverage: Double,
    val mediaType: String // "MOVIE" o "SERIES"
)

// Extensión para convertir MediaItemEntity a MediaItem
fun MediaItemEntity.toDomain(): MediaItem {
    return MediaItem(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        type = MediaType.valueOf(mediaType)
    )
}

// Extensión para convertir MediaItem a MediaItemEntity
fun MediaItem.toEntity(): MediaItemEntity {
    return MediaItemEntity(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        mediaType = type.name
    )
}