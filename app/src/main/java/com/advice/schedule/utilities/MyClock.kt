package com.advice.schedule.utilities

import com.shortstack.hackertracker.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

data class MyClock(val value: Int = 0)

fun MyClock.now(): Date {
    if (BuildConfig.DEBUG) {
        return parse("2022-08-13T01:00:00.000-0000")
    }
    return Date()
}

private fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}


fun getDateMidnight(date: Date, zone: String? = null): Date {
    val apply = Calendar.getInstance().apply {


        time = date

        if (zone != null) {
//        if (App.instance.storage.forceTimeZone) {
//            val timezone =
//                App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"
            timeZone = TimeZone.getTimeZone(zone)
//        }
        }

        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return apply.time
}