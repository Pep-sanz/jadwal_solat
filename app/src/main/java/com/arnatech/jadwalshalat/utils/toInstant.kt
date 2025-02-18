package com.arnatech.jadwalshalat.utils

import android.util.Log
import kotlinx.datetime.*

fun toInstant(timeString: String): Instant {
    Log.i("CONVERT:", timeString)

    // Mendapatkan tanggal hari ini dari sistem
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Parsing jam dan menit dari string
    val (hour, minute) = timeString.split(":").map { it.toInt() }

    // Membuat LocalDateTime dengan waktu yang diinginkan
    val localDateTime = LocalDateTime(today.year, today.month, today.dayOfMonth, hour, minute)

    // Konversi ke Instant berdasarkan zona waktu sistem
    val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())

    return instant
}