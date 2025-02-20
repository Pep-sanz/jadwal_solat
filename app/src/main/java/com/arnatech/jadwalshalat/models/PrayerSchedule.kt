package com.arnatech.jadwalshalat.models

data class PrayerSchedule(
    val date: String? = null,
    val asr: String? = null,
    val sunrise: String? = null,
    val isha: String? = null,
    val dhuhr: String? = null,
    val fajr: String? = null,
    val maghrib: String? = null
)