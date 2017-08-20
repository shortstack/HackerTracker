package com.shortstack.hackertracker

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.isToday(): Boolean {
    val current = Calendar.getInstance()

    val cal = Calendar.getInstance()
    cal.time = this

    return cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)
            && cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
}

fun Date.isTomorrow(): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.roll(Calendar.DAY_OF_YEAR, true)

    val cal2 = Calendar.getInstance()
    cal2.time = this

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isSoonish(SOON_DAYS_AMOUNT: Int): Boolean {
    val cal1 = Calendar.getInstance()

    val cal2 = Calendar.getInstance()
    cal2.time = this
    cal2.roll(Calendar.DAY_OF_YEAR, SOON_DAYS_AMOUNT)

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) <= cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS);
}

@SuppressLint("SimpleDateFormat")
fun Calendar.format8601(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(this.time)

fun <T> kotlin.Array<out T>.joinSQLOr(): String { return joinToString(prefix = " (", separator = " OR ", postfix = ") ") }

fun String.concat(text: String): String {
    if (this.isNullOrEmpty())
        return text
    return this + text
}