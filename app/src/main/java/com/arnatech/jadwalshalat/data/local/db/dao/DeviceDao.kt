package com.arnatech.jadwalshalat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnatech.jadwalshalat.data.local.table.DeviceTable

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device_table LIMIT 1")
    fun getDeviceId(): DeviceTable?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevice(device: DeviceTable)
}
