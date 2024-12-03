package com.example.mapnavigationapp.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapnavigationapp.model.Marker
import com.example.mapnavigationapp.viewmodel.MapViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline

class MapScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().apply {
            load(
                applicationContext,
                applicationContext.getSharedPreferences("osm_prefs", MODE_PRIVATE)
            )
            userAgentValue = packageName
        }

        val viewModel = MapViewModel()

        setContent {
            AppNavigation(viewModel)
        }
    }
}

//// Initialize OSM configuration
//Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(viewModel: MapViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val filteredMarkers = viewModel.filteredMarkers
    val routes = viewModel.routes

    var currentRouteMarkers by remember { mutableStateOf<List<Marker>>(emptyList()) }
    Configuration.getInstance()
        .load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))

    fun addRoute(marker: Marker) {
        if (currentRouteMarkers.isEmpty()) {
            currentRouteMarkers = listOf(marker)
        } else {
            currentRouteMarkers = currentRouteMarkers + marker
            if (currentRouteMarkers.size == 2) {
                viewModel.createRoute(currentRouteMarkers[0], currentRouteMarkers[1])
                currentRouteMarkers = emptyList()
            }
        }
    }

    var searchText by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawer Section for search and buttons
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = false, // Disables gestures (closing with gesture in conflict with map)
            drawerContent = {
                // Close Button inside the drawer to close the drawer
                IconButton(
                    onClick = { coroutineScope.launch { drawerState.close() } },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Drawer")
                }

                // Search Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search Marker") },
                        placeholder = { Text("Enter marker name...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Button Row - Filter and Add Marker
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.filterMarkers(searchText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            // enabled = searchText.isNotEmpty() // Turned off - clearing filtering
                        ) {
                            Text("Filter")
                        }

                        Button(
                            onClick = { navController.navigate("manage_marker/-1") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Add Marker")
                        }
                    }
                }
            },
            content = {
                // Map Section
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter)
                ) {
                    it.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                    it.setMultiTouchControls(true)
                    it.controller.setZoom(15.0)
                    it.controller.setCenter(GeoPoint(51.5, -0.1)) // Default map center (London)

                    val overlayItems = filteredMarkers.map { marker ->
                        OverlayItem(marker.title, "", GeoPoint(marker.latitude, marker.longitude))
                    }
                    val overlay = ItemizedIconOverlay(
                        overlayItems,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(
                                index: Int,
                                item: OverlayItem?
                            ): Boolean {
                                Toast.makeText(
                                    context,
                                    "Tapped on ${item?.title}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                                val tappedMarker = filteredMarkers[index]
                                addRoute(tappedMarker) // Add to current route
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean =
                                false
                        },
                        context
                    )
                    it.overlays.clear()
                    it.overlays.add(overlay)

                    routes.forEach { route ->
                        val start = route.markers.first()
                        val end = route.markers.last()
                        val line = Polyline().apply {
                            addPoint(GeoPoint(start.latitude, start.longitude))
                            addPoint(GeoPoint(end.latitude, end.longitude))
                        }
                        it.overlays.add(line)
                    }

                    if (currentRouteMarkers.size == 2) {
                        val line = Polyline().apply {
                            addPoint(
                                GeoPoint(
                                    currentRouteMarkers[0].latitude,
                                    currentRouteMarkers[0].longitude
                                )
                            )
                            addPoint(
                                GeoPoint(
                                    currentRouteMarkers[1].latitude,
                                    currentRouteMarkers[1].longitude
                                )
                            )
                        }
                        it.overlays.add(line)
                    }
                }
            }
        )

        // Trash Icon Button
        IconButton(
            onClick = {
                viewModel.clearRoutes()
                Toast.makeText(context, "Route Cleared", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Route")
        }

        // Button to open the drawer (opens the search and filter panel)
        if (!drawerState.isOpen) { // Show menu icon only when drawer is closed
            IconButton(
                onClick = { coroutineScope.launch { drawerState.open() } },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Drawer")
            }
        }
    }
}









