package com.arnatech.jadwalshalat.data.remote.api

import com.arnatech.jadwalshalat.data.remote.response.DeviceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("device/tv-content?uuid=id")
    suspend fun getContentByDeviceId(@Query("uuid") uuid: String): DeviceResponse
}