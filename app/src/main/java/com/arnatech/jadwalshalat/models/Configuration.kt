package com.arnatech.jadwalshalat.models

data class Configuration(
    val id: Int,
    val mosque: Int? = null,
    val maxSliders: Int? = null,
    val maxTextMarquee: Int? = null,
    val prayerDurationDays: Int? = null,
    val allowCalendarAccess: Boolean? = null,
    val createdAt: String? = null,
)