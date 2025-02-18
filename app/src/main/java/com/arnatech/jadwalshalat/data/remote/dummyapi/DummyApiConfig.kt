package com.arnatech.jadwalshalat.data.remote.dummyapi

import com.arnatech.jadwalshalat.data.remote.api.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DummyApiConfig {
    private val client = OkHttpClient.Builder()
        .addInterceptor(MockResponseInterceptor()) // Tambahkan interceptor
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://masjid-display.arnatech.id/api/") // Base URL hanya sebagai placeholder
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}