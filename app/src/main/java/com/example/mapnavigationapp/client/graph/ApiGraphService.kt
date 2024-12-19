package com.example.mapnavigationapp.client.graph

import com.example.mapnavigationapp.dto.graph.geocoding.GeocodingResultResponse
import com.example.mapnavigationapp.dto.graph.route.RouteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiGraphService {

    @GET("geocode")
    fun geocode(
        @Query("q") searchPhrase: String,
        @Query("locale") locale: String,
    ): Call<GeocodingResultResponse>

    @GET("route")
    fun getRoute(
        @Query("point") points: List<String>,
        @Query("vehicle") vehicle: String = "car",
    ): Call<RouteResponse>

    companion object {

        private var apiGraphService: ApiGraphService? = null

        fun getInstance(): ApiGraphService {
            if (apiGraphService == null) {
                apiGraphService = RetrofitGraphInstance
                    .getInstance()
                    .create(ApiGraphService::class.java)
            }
            return apiGraphService!!
        }

    }

}