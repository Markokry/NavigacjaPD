package com.example.mapnavigationapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapnavigationapp.model.Marker
import com.example.mapnavigationapp.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerManagementScreen(
    navController: NavController,
    viewModel: MapViewModel,
    markerId: Int? = null
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    val markerToEdit = markerId?.let { id ->
        viewModel.filteredMarkers.find { it.id == id }
    }

    LaunchedEffect(markerId) {
        markerToEdit?.let { marker ->
            latitude = marker.latitude.toString()
            longitude = marker.longitude.toString()
            title = marker.title
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") }
        )
        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (markerId != null) {
                    viewModel.updateMarker(
                        Marker(markerId, latitude.toDouble(), longitude.toDouble(), title)
                    )
                } else {
                    viewModel.addMarker(
                        Marker(
                            id = (viewModel.filteredMarkers.maxOfOrNull { it.id } ?: 0) + 1,
                            latitude = latitude.toDouble(),
                            longitude = longitude.toDouble(),
                            title = title
                        )
                    )
                }
                navController.popBackStack()
            }) {
                Text(if (markerId != null) "Update Marker" else "Add Marker")
            }
            if (markerId != null) {
                Button(onClick = {
                    viewModel.deleteMarker(markerId)
                    navController.popBackStack()
                }) {
                    Text("Delete Marker")
                }
            }
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Cancel")
        }
    }
}
