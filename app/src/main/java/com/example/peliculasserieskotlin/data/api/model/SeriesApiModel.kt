package com.example.peliculasserieskotlin.data.api.model

import com.google.gson.annotations.SerializedName

data class SeriesApiModel(
    val id: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("first_air_date")
    val firstAirDate: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double?
)

data class SeriesApiResponse(
    val results: List<SeriesApiModel>
)
