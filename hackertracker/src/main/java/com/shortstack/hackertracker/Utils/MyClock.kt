package com.shortstack.hackertracker.utils

import java.util.*

data class MyClock(val value: Int = 0)

fun MyClock.now(): Date {
    return Date()
}