package com.arnatech.jadwalshalat.models

data class DeviceData (
    val mosque: Mosque?,
    val prayerSchedule: List<PrayerSchedule>?,
    val sliders: List<Slider>?,
    val textMarquee: List<TextMarquee>?,
    val configuration: Configuration?,
)