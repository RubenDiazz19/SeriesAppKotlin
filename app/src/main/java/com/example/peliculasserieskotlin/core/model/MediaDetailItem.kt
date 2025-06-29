package com.example.peliculasserieskotlin.core.model

// Modelo base para detalles de media
sealed class MediaDetailItem(
    open val id: Int,
    open val title: String,
    open val overview: String,
    open val posterUrl: String,
    open val backdropUrl: String?,
    open val voteAverage: Double,
    open val type: MediaType
) {
    data class MovieDetailItem(
        override val id: Int,
        override val title: String,
        override val overview: String,
        override val posterUrl: String,
        override val backdropUrl: String?,
        override val voteAverage: Double,
        override val type: MediaType = MediaType.MOVIE,

        val originalTitle: String?,
        val releaseDate: String?,
        val voteCount: Int?,
        val runtime: Int?,
        val budget: Long?,
        val revenue: Long?,
        val genres: List<GenreItem>?,
        val productionCompanies: List<ProductionCompanyItem>?,
        val productionCountries: List<ProductionCountryItem>?,
        val spokenLanguages: List<SpokenLanguageItem>?,
        val status: String?,
        val tagline: String?
    ) : MediaDetailItem(id, title, overview, posterUrl, backdropUrl, voteAverage, type)

    data class SeriesDetailItem(
        override val id: Int,
        override val title: String,
        override val overview: String,
        override val posterUrl: String,
        override val backdropUrl: String?,
        override val voteAverage: Double,
        override val type: MediaType = MediaType.SERIES,

        val originalTitle: String?,
        val firstAirDate: String?,
        val voteCount: Int?,
        val runtime: Int?, // Duración promedio de episodio
        val numberOfSeasons: Int?,
        val numberOfEpisodes: Int?,
        val genres: List<GenreItem>?,
        val productionCompanies: List<ProductionCompanyItem>?,
        val productionCountries: List<ProductionCountryItem>?,
        val spokenLanguages: List<SpokenLanguageItem>?,
        val status: String?,
        val tagline: String?
    ) : MediaDetailItem(id, title, overview, posterUrl, backdropUrl, voteAverage, type)
}

// Modelos auxiliares (pueden estar en archivos separados si ya existen)
data class ProductionCompanyItem(val name: String, val logoPath: String?, val originCountry: String)
data class ProductionCountryItem(val iso_3166_1: String, val name: String)
data class SpokenLanguageItem(val englishName: String, val iso_639_1: String, val name: String)