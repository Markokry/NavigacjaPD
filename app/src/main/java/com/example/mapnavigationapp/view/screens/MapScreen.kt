package com.example.mapnavigationapp.view.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapnavigationapp.dto.view.MarkerDTO
import com.example.mapnavigationapp.view.AppNavigation
import com.example.mapnavigationapp.view.components.location.LocationSearchResult
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

    val mapCenter = viewModel.mapCenter

    val routePoints by rememberUpdatedState(viewModel.routePoints)

    var currentRouteMarkers by remember { mutableStateOf<List<MarkerDTO>>(emptyList()) }
    Configuration.getInstance()
        .load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))

    fun addRoute(marker: MarkerDTO) {
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

    val locationSearchText by viewModel.locationSearchText.collectAsState()
    val locationIsSearching by viewModel.locationIsSearching.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawer Section for search and buttons
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false, // Disables gestures (closing with gesture in conflict with map)
            drawerContent = {

                // Search Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Close Button inside the drawer to close the drawer
                    IconButton(
                        onClick = { coroutineScope.launch { drawerState.close() } },
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Drawer")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SearchBar(
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = locationSearchText,
                                onQueryChange = viewModel::onLocationSearchTextChange,
                                onSearch = viewModel::onLocationSearchTextChange,
                                expanded = locationIsSearching,
                                onExpandedChange = { viewModel.onLocationToggleSearch() },
                                enabled = true,
                                placeholder = {
                                    Text(text = "Search for locations...")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {},
                                interactionSource = null,
                            )
                        },
                        modifier = Modifier,
                        shape = SearchBarDefaults.inputFieldShape,
                        colors = SearchBarDefaults.colors(),
                        expanded = locationIsSearching,
                        onExpandedChange = { viewModel.onLocationToggleSearch() },
                        tonalElevation = 0.dp,
                        shadowElevation = SearchBarDefaults.ShadowElevation,
                        windowInsets = SearchBarDefaults.windowInsets
                    ) {
                        LazyColumn {
                            items(count = viewModel.locations.size,
                                key = { index -> index },
                                itemContent = { index ->
                                    val location = viewModel.locations[index]
                                    LocationSearchResult(
                                        result = location,
                                        onClick = {
                                            val marker =                                                 MarkerDTO(
                                                id = (viewModel.filteredMarkers.maxOfOrNull { it.id } ?: 0) + 1,
                                                latitude = location.point.lat,
                                                longitude = location.point.lng,
                                                title = location.name
                                            )
                                            viewModel.addMarker(marker)
                                            viewModel.centerMap(marker)
                                            navController.navigate("map")
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(
                                top = 12.dp,
                                bottom = 12.dp
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Button Row - Filter and Add Marker
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Nowy przycisk do Route Planner
                        Button(
                            onClick = { navController.navigate("route_planner") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Route Planner")
                        }

                        // lista markerow
                        Button(
                            onClick = { navController.navigate("markers_list") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Markers List")
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
                    it.controller.setCenter(mapCenter.value) // Default map center

                    // Overlay markerów
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
                                ).show()

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

                    if (routePoints.isNotEmpty()) {
                        val routeLine = Polyline().apply {
                            setPoints(routePoints)
                        }
                        it.overlays.add(routeLine)
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









