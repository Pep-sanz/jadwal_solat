package com.arnatech.jadwalshalat.deviceRepository

import android.content.Context
import com.arnatech.jadwalshalat.appDatabase.AppDatabase
import com.arnatech.jadwalshalat.models.DeviceId
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository(context: Context) {
    private val deviceDao = AppDatabase.getDatabase(context).deviceDao()

    suspend fun getOrCreateDeviceId(): String {
        return withContext(Dispatchers.IO) {
            var device = deviceDao.getDeviceId()

            if (device == null) {
                val newId = UUID.randomUUID().toString()
                device = DeviceId(newId)
                deviceDao.insertDeviceId(device)
            }

            device.id
        }
    }
}
