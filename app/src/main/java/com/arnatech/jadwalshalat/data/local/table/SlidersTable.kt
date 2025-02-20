package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sliders_table")
data class SlidersTable(
    @PrimaryKey val id: Int?,
    val mosque: Int?,
    @Embedded val backgroundImage: BackgroundImageEntity?,
    val text: String?,
    val createdAt: String?
)

data class BackgroundImageEntity(
    val backgroundId: Int?,
    val name: String?,
    val url: String?,
    val fileSize: Int?
)