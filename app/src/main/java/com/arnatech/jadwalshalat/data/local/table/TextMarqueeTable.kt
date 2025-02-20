package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_marquee_table")
data class TextMarqueeTable(
    @PrimaryKey val id: Int? = null,
    val mosque: Int? = null,
    val createdAt: String? = null,
    val text: String? = null
)
