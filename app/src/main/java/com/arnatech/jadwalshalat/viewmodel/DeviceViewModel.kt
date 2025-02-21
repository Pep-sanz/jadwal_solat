package com.arnatech.jadwalshalat.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arnatech.jadwalshalat.data.DeviceRepository
import com.arnatech.jadwalshalat.data.Result
import com.arnatech.jadwalshalat.models.DeviceData
import kotlinx.coroutines.launch

class DeviceViewModel(private val deviceRepository: DeviceRepository) : ViewModel() {
    private val _deviceId = MutableLiveData<String>()
    val deviceId: LiveData<String> = _deviceId

    private val _deviceData = MutableLiveData<Result<DeviceData>>()
    val deviceData: LiveData<Result<DeviceData>> = _deviceData

    fun getOrCreateDeviceId() {
        viewModelScope.launch {
            val localDeviceId = deviceRepository.getOrCreateDeviceId()
            _deviceId.postValue(localDeviceId)
        }
    }

    fun getDeviceData(owner: LifecycleOwner, deviceId: String) {
        val localDeviceData = deviceRepository.getDeviceData(deviceId)
        localDeviceData.observe(owner) { result ->
            _deviceData.value = result
        }
    }
}