package com.example.mapnavigationapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapnavigationapp.MyApplication
import com.example.mapnavigationapp.database.AppDatabase
import com.example.mapnavigationapp.model.Marker
import com.example.mapnavigationapp.model.MarkerEntity
import com.example.mapnavigationapp.model.Route
import com.example.mapnavigationapp.model.RouteEntity
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val db: AppDatabase = AppDatabase.getDatabase(MyApplication.getAppContext())

    val filteredMarkers = mutableStateListOf<Marker>()
    val routes = mutableListOf<Route>()

    init {
        initializeSampleMarkers()
    }

    fun initializeSampleMarkers() {
        viewModelScope.launch {
            val existingMarkers = db.mapDao().getAllMarkers()

            if (existingMarkers.isEmpty()) {
                val sampleMarkers = listOf(
                    MarkerEntity(1, 51.5074, -0.1278, "Big Ben"),
                    MarkerEntity(2, 51.5033, -0.1195, "London Eye"),
                    MarkerEntity(3, 51.5155, -0.0922, "St. Paul's Cathedral"),
                    MarkerEntity(4, 51.5007, -0.1246, "Westminster Abbey"),
                    MarkerEntity(5, 51.5145, -0.0781, "The Gherkin")
                )
                db.mapDao().insertMarkers(sampleMarkers)
                fetchMarkersFromDatabase()
            } else {
                fetchMarkersFromDatabase()
            }
        }
    }

    fun addMarker(marker: Marker) {
        viewModelScope.launch {
            db.mapDao().insertMarker(
                MarkerEntity(marker.id, marker.latitude, marker.longitude, marker.title)
            )
            fetchMarkersFromDatabase()
        }
    }

    fun updateMarker(marker: Marker) {
        viewModelScope.launch {
            db.mapDao().updateMarker(
                MarkerEntity(marker.id, marker.latitude, marker.longitude, marker.title)
            )
            fetchMarkersFromDatabase()
        }
    }

    fun deleteMarker(markerId: Int) {
        viewModelScope.launch {
            val marker = db.mapDao().getAllMarkers().find { it.id == markerId }
            marker?.let {
                db.mapDao().deleteMarker(it)
                fetchMarkersFromDatabase()
            }
        }
    }

    private fun fetchMarkersFromDatabase() {
        viewModelScope.launch {
            val markers = db.mapDao().getAllMarkers().map { marker ->
                Marker(marker.id, marker.latitude, marker.longitude, marker.title)
            }
            filteredMarkers.clear()
            filteredMarkers.addAll(markers)
        }
    }

    fun filterMarkers(title: String) {
        viewModelScope.launch {
            val filtered = db.mapDao().getAllMarkers().filter {
                it.title.contains(title, ignoreCase = true)
            }
            filteredMarkers.clear()
            filteredMarkers.addAll(filtered.map {
                Marker(it.id, it.latitude, it.longitude, it.title)
            })
        }
    }

    fun createRoute(startMarker: Marker, endMarker: Marker) {
        val route = Route(
            id = routes.size + 1,
            name = "Route from ${startMarker.title} to ${endMarker.title}",
            markers = listOf(startMarker, endMarker)
        )
        routes.add(route)

        viewModelScope.launch {
            db.mapDao().insertRoute(RouteEntity(route.id, route.name))
        }
    }

    fun clearRoutes() {
        routes.clear()
    }
}