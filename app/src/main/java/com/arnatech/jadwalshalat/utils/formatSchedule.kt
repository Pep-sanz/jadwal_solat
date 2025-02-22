package com.arnatech.jadwalshalat.utils

fun formatSchedule(schedule: String?): String? {
    if (schedule != null) {
        val splittedSchedule = schedule.split(":")
        return if (splittedSchedule.size > 3) {
            "${splittedSchedule[0]}:${splittedSchedule[1]}"
        } else {
            schedule
        }
    }

    return null
}