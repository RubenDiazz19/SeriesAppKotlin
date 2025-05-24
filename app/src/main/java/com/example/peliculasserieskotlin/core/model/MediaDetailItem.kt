package com.example.peliculasserieskotlin.core.model

/**
 * Modelo extendido que contiene todos los detalles de un elemento multimedia (película o serie).
 * Hereda de MediaItem para mantener compatibilidad con el código existente.
 */
data class MediaDetailItem(
    // Propiedades heredadas - deben ser declaradas con override y val
    override val id: Int,
    override val title: String,
    override val overview: String,
    override val posterUrl: String,
    override val backdropUrl: String?, // Coincide con MediaItem (String?)
    override val voteAverage: Double,   // Coincide con MediaItem (Double)
    override val type: MediaType,

    // Propiedades adicionales de detalle
    val originalTitle: String?,
    val releaseDate: String?,
    val voteCount: Int?,
    val runtime: Int?,              // Duración en minutos
    val budget: Long?,
    val revenue: Long?,
    val genres: List<GenreItem>?, // Este es el campo correcto para los géneros
    val productionCompanies: List<ProductionCompanyItem>?,
    val productionCountries: List<ProductionCountryItem>?,
    val spokenLanguages: List<SpokenLanguageItem>?,
    val status: String?,
    val tagline: String?
) : MediaItem(id, title, overview, posterUrl, backdropUrl, voteAverage, type)

// Clases de datos para los campos anidados (si aún no existen en otro lugar)
// Asegúrate de que estas definiciones no entren en conflicto con otras existentes.
data class GenreItem(val id: Int, val name: String)
data class ProductionCompanyItem(val name: String, val logoPath: String?, val originCountry: String)
data class ProductionCountryItem(val iso_3166_1: String, val name: String)
data class SpokenLanguageItem(val englishName: String, val iso_639_1: String, val name: String)