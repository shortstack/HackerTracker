package com.shortstack.hackertracker

import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.now
import io.mockk.every
import io.mockk.mockkStatic
import java.text.SimpleDateFormat
import java.util.*

fun setCurrentClock(date: String) {
    mockkStatic("com.shortstack.hackertracker.utils.MyClockKt")
    every {
        MyClock().now()
    } returns parse(date)
}

fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}
