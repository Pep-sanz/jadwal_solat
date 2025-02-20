package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuration_table")
data class ConfigurationTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val mosque: Int? = null,
    val maxSliders: Int? = null,
    val maxTextMarquee: Int? = null,
    val prayerDurationDays: Int? = null,
    val allowCalendarAccess: Boolean? = null,
    val createdAt: String? = null,
)
