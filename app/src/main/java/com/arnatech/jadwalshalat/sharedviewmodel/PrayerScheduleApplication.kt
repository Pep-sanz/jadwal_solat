package com.arnatech.jadwalshalat.sharedviewmodel

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.arnatech.jadwalshalat.factory.ViewModelFactory

class PrayerScheduleApplication : Application(), ViewModelStoreOwner {
    private val appViewModelStore: ViewModelStore by lazy { ViewModelStore() }
    private lateinit var factory: ViewModelFactory

    override fun onCreate() {
        super.onCreate()
        factory = ViewModelFactory.getInstance(this)
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    fun getViewModelFactory(): ViewModelFactory = factory
}