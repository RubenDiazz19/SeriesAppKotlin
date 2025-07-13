package com.example.seriesappkotlin.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Constantes y utilidades comunes para los modelos de medios.
 */
object MediaConstants {
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
    const val DEFAULT_POSTER_SIZE = "w500"
    const val DEFAULT_BACKDROP_SIZE = "original"
    
    private val dateFormatter = DateTimeFormatter.ISO_DATE

    /**
     * Formatea una URL de imagen de TMDB.
     * @param path Ruta de la imagen
     * @param size Tamaño de la imagen (por defecto w500)
     * @return URL completa de la imagen
     */
    fun formatImageUrl(path: String?, size: String = DEFAULT_POSTER_SIZE): String {
        return path?.let { "$TMDB_IMAGE_BASE_URL/$size$it" } ?: ""
    }

    /**
     * Parsea una fecha en formato ISO a LocalDate.
     * @param dateString Fecha en formato String
     * @return LocalDate o null si la fecha es inválida
     */
    fun parseDate(dateString: String?): LocalDate? {
        return try {
            dateString?.let { LocalDate.parse(it, dateFormatter) }
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Enumeración para los estados posibles de una película o serie.
 */
enum class MediaStatus {
    RUMORED,
    PLANNED,
    IN_PRODUCTION,
    POST_PRODUCTION,
    RELEASED,
    CANCELED,
    UNKNOWN;

    companion object {
        fun fromString(value: String?): MediaStatus {
            return try {
                value?.uppercase()?.let { valueOf(it) } ?: UNKNOWN
            } catch (e: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
} 