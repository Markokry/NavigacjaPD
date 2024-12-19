package com.example.mapnavigationapp.client.graph;

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitGraphInstance {

    private const val BASE_URL = "https://graphhopper.com/api/1/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .client(
                HttpGraphClient.httpGraphClient
            )
            .build()
    }

}
