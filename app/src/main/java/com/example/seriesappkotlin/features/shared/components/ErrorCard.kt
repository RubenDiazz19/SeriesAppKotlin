package com.example.seriesappkotlin.features.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorCard(
    message: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 14,
    padding: Int = 16
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = message,
            color = Color.Red.copy(alpha = 0.8f),
            fontSize = fontSize.sp,
            modifier = Modifier.padding(padding.dp),
            textAlign = TextAlign.Center
        )
    }
} 