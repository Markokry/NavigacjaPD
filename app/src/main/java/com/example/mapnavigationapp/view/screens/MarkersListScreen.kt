package com.example.mapnavigationapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapnavigationapp.view.components.marker.MarkerListItem
import com.example.mapnavigationapp.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkersListScreen(viewModel: MapViewModel, navController: NavController) {
    val context = LocalContext.current
    val markers = viewModel.filteredMarkers

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        markers.forEach { marker ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MarkerListItem(
                    marker = marker,
                    onDelete = { viewModel.deleteMarker(marker.id) },
                    onClick = {
                        viewModel.centerMap(marker)
                        navController.navigate("map")
                    },
                )
                HorizontalDivider()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("manage_marker/-1") },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add Marker")
            }

            Button(
                onClick = { navController.navigate("route_planner") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Route Planner")
            }

            HorizontalDivider()

            Button(
                onClick = { navController.navigate("map") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back to map")
            }

        }
    }
}