package com.example.seriesappkotlin.core.database.entity

import androidx.room.Entity

@Entity(tableName = "watched_episodes", primaryKeys = ["userId", "serieId", "seasonNumber", "episodeNumber"])
data class WatchedEpisodeEntity(
    val userId: Int,
    val serieId: Int,
    val seasonNumber: Int,
    val episodeNumber: Int
) 