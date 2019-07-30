package com.shortstack.hackertracker.utils

import com.shortstack.hackertracker.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

data class MyClock(val value: Int = 0)

fun MyClock.now(): Date {
    return Date()
}

private fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}
