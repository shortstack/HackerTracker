package com.advice.schedule.utilities

import com.shortstack.hackertracker.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

data class MyClock(val value: Int = 0)

fun MyClock.now(): Date {
    if (BuildConfig.DEBUG) {
        return parse("2019-06-01T12:00:00.000-0000")
    }
    return Date()
}

private fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}
