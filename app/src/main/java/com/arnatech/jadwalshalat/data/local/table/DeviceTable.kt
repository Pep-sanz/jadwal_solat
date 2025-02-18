package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class DeviceTable(
    @PrimaryKey val id: String
)