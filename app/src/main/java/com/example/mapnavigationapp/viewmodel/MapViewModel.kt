package com.example.mapnavigationapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapnavigationapp.MyApplication
import com.example.mapnavigationapp.client.graph.ApiGraphService
import com.example.mapnavigationapp.database.AppDatabase
import com.example.mapnavigationapp.dto.graph.geocoding.GeocodingResultResponse
import com.example.mapnavigationapp.dto.graph.geocoding.LocationHitResponse
import com.example.mapnavigationapp.dto.graph.route.RouteResponse
import com.example.mapnavigationapp.dto.view.MarkerDTO
import com.example.mapnavigationapp.dto.view.RouteDTO
import com.example.mapnavigationapp.entity.MarkerEntity
import com.example.mapnavigationapp.entity.RouteEntity
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel : ViewModel() {

    private val db: AppDatabase = AppDatabase.getDatabase(MyApplication.getAppContext())

    // czy wyszukiwanie
    private val _locationIsSearching = MutableStateFlow(false)
    val locationIsSearching = _locationIsSearching.asStateFlow()

    // co wpisal Użytkownik
    private val _locationSearchText = MutableStateFlow("")
    val locationSearchText = _locationSearchText.asStateFlow()

    // filtrowanie lokalizacji po API
    private val _locations = mutableStateListOf<LocationHitResponse>()
    val locations: List<LocationHitResponse>
        get() = _locations

    val filteredMarkers = mutableStateListOf<MarkerDTO>()
    val routes = mutableListOf<RouteDTO>()

    val mapCenter = mutableStateOf<GeoPoint>(
        GeoPoint(51.5, -0.1)
    )

    private val _routePoints = mutableStateListOf<GeoPoint>()
    val routePoints: List<GeoPoint> = _routePoints

    init {
        initializeSampleMarkers()
    }

    fun fetchRoute(start: GeoPoint, end: GeoPoint) {
        viewModelScope.launch {
            try {
                val response = ApiGraphService.getInstance().getRoute(
                    points = listOf(
                        "${start.latitude},${start.longitude}",
                        "${end.latitude},${end.longitude}"
                    )
                ).enqueue(
                    object : Callback<RouteResponse> {
                        override fun onResponse(
                            call: Call<RouteResponse>,
                            response: Response<RouteResponse>
                        ) {
                            if (response.isSuccessful) {
                                val result: RouteResponse? = response.body()
                                if (result != null) {
                                    if (result.paths.isNotEmpty()) {
                                        val path = result.paths[0]
                                        _routePoints.clear()

                                        val decodedPoints = PolyUtil.decode(path.points)

                                        decodedPoints.forEach { point ->
                                            _routePoints.add(GeoPoint(point.latitude, point.longitude))
                                        }
                                    }
                                } else {
                                    Log.e(
                                        "MapNavigationApp",
                                        "Route fetching error occurred... Response body is blank...",
                                    )
                                }
                            } else {
                                Log.e(
                                    "MapNavigationApp",
                                    "Route fetching occurred... Response body is not successful...",
                                )
                            }
                        }

                        override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                            Log.e(
                                "MapNavigationApp",
                                "Route fetching error occurred... Call failure...",
                                t
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(
                    "MapNavigationApp",
                    "Geocoding error occurred...",
                    e
                )
            }
        }
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

    fun addMarker(marker: MarkerDTO) {
        viewModelScope.launch {
            db.mapDao().insertMarker(
                MarkerEntity(marker.id, marker.latitude, marker.longitude, marker.title)
            )
            fetchMarkersFromDatabase()
        }
    }

    fun updateMarker(marker: MarkerDTO) {
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

    fun centerMap(marker: MarkerDTO) {
        viewModelScope.launch {
            mapCenter.value = GeoPoint(marker.latitude, marker.longitude)
        }
    }

    private fun fetchMarkersFromDatabase() {
        viewModelScope.launch {
            val markers = db.mapDao().getAllMarkers().map { marker ->
                MarkerDTO(marker.id, marker.latitude, marker.longitude, marker.title)
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
                MarkerDTO(it.id, it.latitude, it.longitude, it.title)
            })
        }
    }

    fun createRoute(startMarker: MarkerDTO, endMarker: MarkerDTO) {
        val route = RouteDTO(
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

    fun onLocationSearchTextChange(text: String) {
        _locationSearchText.value = text
        getLocationsFromAPI()
    }

    fun onLocationToggleSearch() {
        _locationIsSearching.value = !_locationIsSearching.value
        if (!_locationIsSearching.value) {
            onLocationSearchTextChange("")
        }
    }

    private fun getLocationsFromAPI() {
        viewModelScope.launch {
            try {
                _locations.clear()
                if (!locationIsSearching.value) {
                    return@launch
                }
                val searchPhrase: String = locationSearchText.value
                if (searchPhrase.isEmpty()) {
                    return@launch
                }
                if (searchPhrase.length < 3) {
                    return@launch
                }
                val apiService = ApiGraphService.getInstance()
                apiService.geocode(
                    searchPhrase = locationSearchText.value,
                    locale = "en"   // jako stala powinno byc
                ).enqueue(
                    object : Callback<GeocodingResultResponse> {
                        override fun onResponse(
                            call: Call<GeocodingResultResponse>,
                            response: Response<GeocodingResultResponse>
                        ) {
                            if (response.isSuccessful) {
                                val result: GeocodingResultResponse? = response.body()
                                if (result != null) {
                                    _locations.addAll(
                                        result.hits
                                    )
                                } else {
                                    Log.e(
                                        "MapNavigationApp",
                                        "Geocoding error occurred... Response body is blank...",
                                    )
                                }
                            } else {
                                Log.e(
                                    "MapNavigationApp",
                                    "Geocoding error occurred... Response body is not successful...",
                                )
                            }
                        }

                        override fun onFailure(call: Call<GeocodingResultResponse>, t: Throwable) {
                            Log.e(
                                "MapNavigationApp",
                                "Geocoding error occurred... Call failure...",
                                t
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e(
                    "MapNavigationApp",
                    "Geocoding error occurred...",
                    e
                )
            }
        }
    }

}