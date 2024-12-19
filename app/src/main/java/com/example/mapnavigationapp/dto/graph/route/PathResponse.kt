package com.example.mapnavigationapp.dto.graph.route

import com.google.gson.annotations.SerializedName


data class PathResponse(

    @SerializedName("distance")
    val distance: Double,

    @SerializedName("time")
    val time: Long,

    @SerializedName("points")
    val points: String

    )