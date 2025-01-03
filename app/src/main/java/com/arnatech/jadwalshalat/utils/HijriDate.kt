package com.arnatech.jadwalshalat.utils

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.text.SimpleDateFormat
import java.util.Locale

class HijriDate {

    fun GetCurrentHijriDate(): String{
        val hijriCalendar = UmmalquraCalendar()
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(hijriCalendar.time)
    }
}