package com.example.peliculasserieskotlin.data.repository

import com.example.peliculasserieskotlin.domain.model.MediaItem
import com.example.peliculasserieskotlin.domain.model.MediaType
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    /**
     * Obtiene una lista popular de MediaItem (películas o series) según el tipo especificado.
     * Reemplaza a getMovies y getSeries.
     * @param page Número de página para la paginación.
     * @param genre Género para filtrar (opcional).
     * @param type Tipo de media (MOVIE o SERIES).
     * @return Un Flow que emite una lista de MediaItem.
     */
    fun getPopularMedia(page: Int, genre: String?, type: MediaType): Flow<List<MediaItem>>

    /**
     * Obtiene una lista de los MediaItem mejor valorados (películas o series) según el tipo especificado.
     * Reemplaza a getTopRatedMovies y getTopRatedSeries.
     * @param page Número de página para la paginación.
     * @param type Tipo de media (MOVIE o SERIES).
     * @return Un Flow que emite una lista de MediaItem.
     */
    fun getTopRatedMedia(page: Int, type: MediaType): Flow<List<MediaItem>>

    /**
     * Obtiene una lista de MediaItem para la sección "descubrir" o similar (películas o series) según el tipo especificado.
     * Esto puede mapearse a la funcionalidad actual de "favoritos" que carga una lista general desde la API.
     * @param page Número de página para la paginación.
     * @param type Tipo de media (MOVIE o SERIES).
     * @return Un Flow que emite una lista de MediaItem.
     */
    fun getDiscoverMedia(page: Int, type: MediaType): Flow<List<MediaItem>>

    /**
     * Busca MediaItem (películas o series) según una consulta y tipo especificado.
     * Reemplaza a searchMovies y searchSeries.
     * @param query Texto de búsqueda.
     * @param page Número de página para la paginación.
     * @param type Tipo de media (MOVIE o SERIES).
     * @return Una lista de MediaItem (resultado de una operación suspendida).
     */
    suspend fun searchMedia(query: String, page: Int, type: MediaType): List<MediaItem>

    /**
     * Inserta una lista de MediaItem en la base de datos local.
     * La implementación deberá diferenciar por MediaItem.type para usar el DAO correcto.
     * Reemplaza a insertMovies e insertSeries.
     * @param mediaItems Lista de MediaItem para insertar.
     */
    suspend fun insertMediaToLocalDb(mediaItems: List<MediaItem>)

    /**
     * Obtiene una lista de MediaItem desde la base de datos local, filtrada por tipo.
     * @param type Tipo de media (MOVIE o SERIES) a obtener.
     * @return Un Flow que emite una lista de MediaItem cacheados del tipo especificado.
     */
    fun getMediaFromLocalDb(type: MediaType): Flow<List<MediaItem>>

    /**
     * Obtiene todos los MediaItem almacenados en la base de datos local.
     * @return Un Flow que emite una lista de todos los MediaItem cacheados.
     */
    fun getAllMediaFromLocalDb(): Flow<List<MediaItem>>
}