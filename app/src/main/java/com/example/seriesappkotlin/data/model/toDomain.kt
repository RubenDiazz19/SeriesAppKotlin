package com.example.seriesappkotlin.data.model

import com.example.seriesappkotlin.core.model.Serie

fun SeriesApiModel.toDomain(): Serie = this.toSerie() 