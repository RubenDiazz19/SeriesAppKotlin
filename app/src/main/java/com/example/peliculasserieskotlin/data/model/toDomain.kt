package com.example.peliculasserieskotlin.data.model

import com.example.peliculasserieskotlin.core.model.Serie

fun SeriesApiModel.toDomain(): Serie = this.toSerie() 