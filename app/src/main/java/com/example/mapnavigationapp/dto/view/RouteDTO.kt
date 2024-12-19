package com.example.mapnavigationapp.dto.view

data class RouteDTO(
    val id: Int,
    val name: String,
    val markers: List<MarkerDTO>
)
