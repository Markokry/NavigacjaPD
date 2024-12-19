package com.example.mapnavigationapp.view.components.marker

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mapnavigationapp.dto.view.MarkerDTO

@Composable
fun MarkerListItem(
    marker: MarkerDTO,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(marker.title) },
        supportingContent = { Text(marker.formatLatLng()) },
        leadingContent = {
            Row {

                Button(onClick = onClick) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Find marker",
                    )
                }

                Button(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete marker",
                    )
                }

            }
        }
    )
}