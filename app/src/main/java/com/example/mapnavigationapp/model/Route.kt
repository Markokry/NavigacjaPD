package com.example.mapnavigationapp.model

data class Route(
    val id: Int,
    val name: String,
    val markers: List<Marker>
)
