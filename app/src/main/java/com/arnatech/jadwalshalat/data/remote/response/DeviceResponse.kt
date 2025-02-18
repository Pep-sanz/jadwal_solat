package com.arnatech.jadwalshalat.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeviceResponse(
	val mosque: Mosque? = null,

	@field:SerializedName("prayer_schedule")
	val prayerSchedule: PrayerSchedule? = null,

	val sliders: List<SlidersItem?>? = null,

	@field:SerializedName("text_marquee")
	val textMarquee: List<TextMarqueeItem?>? = null,

	val configurations: Configurations? = null,

)

data class Mosque(
	val address: String? = null,
	val latitude: Any? = null,
	val name: String? = null,
	val longitude: Any? = null
)

data class PrayerSchedule(
	val date: String? = null,
	val asr: String? = null,
	val sunrise: String? = null,
	val isha: String? = null,
	val dhuhr: String? = null,
	val fajr: String? = null,
	val maghrib: String? = null
)

data class SlidersItem(
	@field:SerializedName("background_image")
	val backgroundImage: String? = null,

	val mosque: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	val id: Int? = null,
	val text: String? = null
)

data class TextMarqueeItem(
	val mosque: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	val id: Int? = null,
	val text: String? = null
)

data class Configurations(
	@field:SerializedName("max_sliders")
	val maxSliders: Int? = null,

	val mosque: Int? = null,

	@field:SerializedName("prayer_duration_days")
	val prayerDurationDays: Int? = null,

	@field:SerializedName("allow_calendar_access")
	val allowCalendarAccess: Boolean? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	val id: Int? = null,

	@field:SerializedName("max_text_marquee")
	val maxTextMarquee: Int? = null
)

