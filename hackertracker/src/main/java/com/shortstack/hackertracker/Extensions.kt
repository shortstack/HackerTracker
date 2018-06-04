package com.shortstack.hackertracker

import com.google.gson.Gson
import com.orhanobut.logger.Logger
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.isToday(): Boolean {
    val current = Calendar.getInstance().now()

    val cal = Calendar.getInstance()
    cal.time = this

    return cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)
            && cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
}

fun Date.isTomorrow(): Boolean {
    val cal1 = Calendar.getInstance().now()
    cal1.roll(Calendar.DAY_OF_YEAR, true)

    val cal2 = Calendar.getInstance()
    cal2.time = this

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isSoonish(SOON_DAYS_AMOUNT: Int): Boolean {
    val cal1 = Calendar.getInstance().now()

    val cal2 = Calendar.getInstance()
    cal2.time = this
    cal2.roll(Calendar.DAY_OF_YEAR, SOON_DAYS_AMOUNT)

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) <= cal2.get(Calendar.DAY_OF_YEAR)
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS);
}


inline fun <reified T> Gson.fromFile(filename: String, root: String): T {
    try {
        val s = "database/conferences/$root/$filename"
        val stream = App.application.assets.open(s)

        val size = stream.available()

        val buffer = ByteArray(size)

        stream.read(buffer)
        stream.close()

        return fromJson(String(buffer), T::class.java)

    } catch (e: IOException) {
        Logger.e(e, "Could not create the database.")
        throw e
    } catch (e: FileNotFoundException) {
        Logger.e("Could not find the file. $root.$filename")
        return fromJson("", T::class.java)
    }
}

fun Date.now(): Date {
    if (BuildConfig.DEBUG)
        time = Constants.DEBUG_FORCE_TIME_DATE

    return this
}

fun Calendar.now(): Calendar {
    this.time = Date().now()
    return this
}