package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mosque_table")
data class MosqueTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
