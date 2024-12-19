package com.example.mapnavigationapp.view.components.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mapnavigationapp.dto.graph.geocoding.LocationHitDTO

@Composable
fun LocationSearchResult(
    result: LocationHitDTO,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 12.dp)
    ) {
        Text(
            text = result.name,
        )
        Text(text = " (" + result.country + ")")
    }
}