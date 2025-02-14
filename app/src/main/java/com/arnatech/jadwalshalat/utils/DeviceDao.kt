package com.arnatech.jadwalshalat.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.models.DeviceId

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device_table LIMIT 1")
    fun getDeviceId(): DeviceId?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeviceId(deviceId: DeviceId)
}
