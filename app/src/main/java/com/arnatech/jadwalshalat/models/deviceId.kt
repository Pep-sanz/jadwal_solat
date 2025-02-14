package com.arnatech.jadwalshalat.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class DeviceId(
    @PrimaryKey val id: String
)