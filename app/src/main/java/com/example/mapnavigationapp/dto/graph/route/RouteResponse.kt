package com.example.mapnavigationapp.dto.graph.route

import com.google.gson.annotations.SerializedName


data class RouteResponse(

    @SerializedName("paths")
    val paths: List<PathResponse>

    )