package com.example.peliculasserieskotlin.features.shared.repository

import com.example.peliculasserieskotlin.core.database.FavoriteDao
import com.example.peliculasserieskotlin.core.database.FavoriteEntity
import com.example.peliculasserieskotlin.core.database.MediaItemDao
import com.example.peliculasserieskotlin.core.database.toDomain
import com.example.peliculasserieskotlin.core.database.toEntity
import com.example.peliculasserieskotlin.core.model.MediaItem
import com.example.peliculasserieskotlin.core.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val mediaItemDao: MediaItemDao
) {

    /*----------------- CRUD favoritos -----------------*/

    suspend fun addFavorite(item: MediaItem) {
        mediaItemDao.insertMediaItem(item.toEntity())                   // asegura que existe
        favoriteDao.insertFavorite(FavoriteEntity(item.id, item.type.name))
    }

    suspend fun removeFavorite(id: Int, type: MediaType) =
        favoriteDao.deleteFavorite(FavoriteEntity(id, type.name))

    fun isFavorite(id: Int, type: MediaType): Flow<Boolean> =
        favoriteDao.isFavorite(id, type.name)

    /*----------------- ⭐ Lista de favoritos -----------------*/
    fun getFavoriteMedia(mediaType: MediaType): Flow<List<MediaItem>> =
        favoriteDao.getFavoritesByType(mediaType.name)                 // emite cambios
            .map { favs ->
                val ids = favs.map { it.mediaId }
                if (ids.isEmpty()) return@map emptyList()

                /* Traemos TODOS los ids sin filtrar por tipo -------- */
                mediaItemDao.getMediaItemsByIds(ids)
                    .map { it.toDomain() }
                    /* Filtramos por el tipo solicitado -------------- */
                    .filter { it.type == mediaType }
                    /* Mantenemos el mismo orden que la lista de ids --*/
                    .sortedBy { ids.indexOf(it.id) }
            }
}