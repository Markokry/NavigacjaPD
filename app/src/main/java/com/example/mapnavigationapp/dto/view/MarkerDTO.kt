package com.example.mapnavigationapp.dto.view

data class MarkerDTO(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String,
) {

    fun formatLatLng(): String {
        return "$latitude, $longitude"
    }

}
