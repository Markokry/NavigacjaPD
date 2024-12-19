package com.example.mapnavigationapp.view.components.location

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mapnavigationapp.dto.graph.geocoding.LocationHitResponse

@Composable
fun LocationSearchResult(
    result: LocationHitResponse,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(result.name) },
        supportingContent = { Text(result.formatLatLng()) },
        leadingContent = {
            Row {

                Button(onClick = onClick) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Add marker",
                    )
                }

            }
        })

}