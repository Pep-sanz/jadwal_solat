package com.arnatech.jadwalshalat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arnatech.jadwalshalat.data.DeviceRepository
import kotlinx.coroutines.launch

class DeviceViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    private val _deviceId = MutableLiveData<String>()
    val deviceId: LiveData<String> = _deviceId

    fun getOrCreateDeviceId() {
        viewModelScope.launch {
            val localDeviceId = deviceRepository.getOrCreateDeviceId()
            _deviceId.postValue(localDeviceId)
        }
    }

    fun getDeviceData(deviceId: String) = deviceRepository.getDeviceData(deviceId)
}