package com.shortstack.hackertracker

import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.json.JSONException
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

    val time = cal1.time.time

    val cal2 = Calendar.getInstance()
    cal2.time = this

    val time2 = cal2.time.time

    val daysInMilliSeconds = SOON_DAYS_AMOUNT * 1000 * 60 * 60 * 24
    return Math.abs(time - time2) < daysInMilliSeconds
}

fun Date.getDateDifference(date: Date, timeUnit: TimeUnit): Long {
    return timeUnit.convert(date.time - this.time, TimeUnit.MILLISECONDS);
}


inline fun <reified T> Gson.fromFile(filename: String, root: String?): T? {
    try {
        val s = if (root != null) {
            "database/conferences/$root/$filename"
        } else {
            "database/conferences/$filename"
        }
        val stream = App.application.assets.open(s)

        val size = stream.available()

        val buffer = ByteArray(size)

        stream.read(buffer)
        stream.close()

        return fromJson(String(buffer), T::class.java)

    } catch (e: FileNotFoundException) {
        Logger.e("Could not find the file. $root/$filename")
        return fromJson("", T::class.java)
    } catch (e: JSONException) {
        Logger.e("Invalid JSON within the file. $root/$filename")
        return null
    } catch (e: IOException) {
        Logger.e(e, "Could not create the database.")
        return null
    }
}

fun Date.now(): Date {
//    if (BuildConfig.DEBUG) {
//        return Calendar.getInstance().now().time
//    }

    return this
}

fun Calendar.now(): Calendar {
//    if (BuildConfig.DEBUG) {
//        val today = Date()
//        today.time = Constants.DEBUG_FORCE_TIME_DATE
//
//        val calendar = Calendar.getInstance()
//        calendar.time = today
//
//        this.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
//        this.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR))
//
//    } else {
    this.time = Date()
//    }

    return this
}