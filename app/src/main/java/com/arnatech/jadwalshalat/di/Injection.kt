package com.arnatech.jadwalshalat.di

import android.content.Context
import com.arnatech.jadwalshalat.data.DeviceRepository
import com.arnatech.jadwalshalat.data.local.db.AppDatabase
import com.arnatech.jadwalshalat.data.remote.api.ApiConfig
import com.arnatech.jadwalshalat.data.remote.dummyapi.DummyApiConfig

object Injection {
    fun provideRepository(context: Context): DeviceRepository {
        val apiService = ApiConfig.getApiService()
        val dummyApiService = DummyApiConfig.instance
        val database = AppDatabase.getDatabase(context)
        return DeviceRepository.getInstance(apiService, dummyApiService, database)
    }
}