package com.example.mapnavigationapp.client.graph

import com.example.mapnavigationapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object HttpGraphClient {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        HttpLoggingInterceptor.Level.BODY
    }

    private fun apiKeyAsQuery(it: Interceptor.Chain) = it.proceed(
        it.request().newBuilder().url(
            it.request().url
                .newBuilder()
                .addQueryParameter("key", BuildConfig.GRAPHHOPPER_API_KEY)
                .build()
        ).build()
    )

    val httpGraphClient: OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor{ apiKeyAsQuery(it) }
            .addInterceptor(httpLoggingInterceptor)
            .build()
}