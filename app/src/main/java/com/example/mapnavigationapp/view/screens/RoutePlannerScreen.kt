package com.example.mapnavigationapp.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapnavigationapp.dto.view.MarkerDTO
import com.example.mapnavigationapp.viewmodel.MapViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlannerScreen(viewModel: MapViewModel, navController: NavController) {
    val context = LocalContext.current
    val markers = viewModel.filteredMarkers
    var startMarker by remember { mutableStateOf<MarkerDTO?>(null) }
    var endMarker by remember { mutableStateOf<MarkerDTO?>(null) }

    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Select Start Point
        Text("Select Start Point", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedStart,
            onExpandedChange = { expandedStart = !expandedStart }
        ) {
            TextField(
                value = startMarker?.title ?: "Select Start Marker",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expandedStart,
                onDismissRequest = { expandedStart = false }
            ) {
                markers.forEach { marker ->
                    DropdownMenuItem(
                        text = { Text(marker.title) },
                        onClick = {
                            startMarker = marker
                            expandedStart = false
                        }
                    )
                }
            }
        }

        // Select End Point
        Text("Select End Point", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedEnd,
            onExpandedChange = { expandedEnd = !expandedEnd }
        ) {
            TextField(
                value = endMarker?.title ?: "Select End Marker",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expandedEnd,
                onDismissRequest = { expandedEnd = false }
            ) {
                markers.forEach { marker ->
                    DropdownMenuItem(
                        text = { Text(marker.title) },
                        onClick = {
                            endMarker = marker
                            expandedEnd = false
                        }
                    )
                }
            }
        }

        Column() {
            Button(
                onClick = {
                    if (startMarker != null && endMarker != null) {
                        viewModel.fetchRoute(
                            GeoPoint(startMarker!!.latitude, startMarker!!.longitude),
                            GeoPoint(endMarker!!.latitude, endMarker!!.longitude)
                        )
                        viewModel.centerMap(startMarker!!)
                        navController.navigate("map")
                    } else {
                        Toast.makeText(context, "Select both points!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = startMarker != null && endMarker != null
            ) {
                Text("Generate Route")
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