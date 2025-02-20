package com.arnatech.jadwalshalat.data.local.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_schedule_table")
data class PrayerScheduleTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String? = null,
    val asr: String? = null,
    val sunrise: String? = null,
    val isha: String? = null,
    val dhuhr: String? = null,
    val fajr: String? = null,
    val maghrib: String? = null
)
