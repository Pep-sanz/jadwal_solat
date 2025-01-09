package com.arnatech.jadwalshalat.utils

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class PrayerTimesHelper {

    fun getPrayerTimes(): PrayerTimes {
//        val coordinates = Coordinates(-6.946127, 107.703734) // Latitude, Longitude
        val coordinates = Coordinates(-7.2069093,107.8767993) // Latitude, Longitude
        val params: CalculationParameters = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters

        val currentDate = Instant.parse(Clock.System.now().toString())
        val date = DateComponents.from(currentDate)

        return PrayerTimes(coordinates, date, params)
    }

    fun formatPrayerTime(prayerTime: Instant?): String {
        return try {
            if (prayerTime != null) {
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("GMT+7")
                formatter.format(Date(prayerTime.toEpochMilliseconds()))
            } else {
                "Waktu tidak tersedia"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

}
