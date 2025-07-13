package com.example.seriesappkotlin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seriesappkotlin.core.database.entity.WatchedEpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedEpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchedEpisode(watchedEpisode: WatchedEpisodeEntity)

    @Query("DELETE FROM watched_episodes WHERE userId = :userId AND serieId = :serieId AND seasonNumber = :seasonNumber AND episodeNumber = :episodeNumber")
    suspend fun deleteWatchedEpisode(userId: Int, serieId: Int, seasonNumber: Int, episodeNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watched_episodes WHERE userId = :userId AND serieId = :serieId AND seasonNumber = :seasonNumber AND episodeNumber = :episodeNumber LIMIT 1)")
    fun isEpisodeWatched(userId: Int, serieId: Int, seasonNumber: Int, episodeNumber: Int): Flow<Boolean>

    @Query("SELECT * FROM watched_episodes WHERE userId = :userId AND serieId = :serieId")
    fun getWatchedEpisodesForSerie(userId: Int, serieId: Int): Flow<List<WatchedEpisodeEntity>>

    @Query("SELECT COUNT(*) FROM watched_episodes WHERE userId = :userId AND serieId = :serieId")
    fun getWatchedEpisodesCount(userId: Int, serieId: Int): Flow<Int>

    @Query("DELETE FROM watched_episodes WHERE userId = :userId AND serieId = :serieId")
    suspend fun deleteAllWatchedEpisodesForSerie(userId: Int, serieId: Int)

    @Query("SELECT DISTINCT serieId FROM watched_episodes WHERE userId = :userId")
    fun getSeriesWithWatchedEpisodes(userId: Int): Flow<List<Int>>
}