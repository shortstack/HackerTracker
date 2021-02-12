package com.shortstack.hackertracker.utilities

import android.annotation.SuppressLint
import android.content.Context
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    @SuppressLint("SimpleDateFormat")
    fun getDateStamp(date: Date): String {
        val format = SimpleDateFormat("MMMM d")

        return format.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(context: Context, date: Date?): String {
        // No start time, return TBA.
        if (date == null)
            return context.getString(R.string.tba)


        val s = if (android.text.format.DateFormat.is24HourFormat(context)) {
            "HH:mm"
        } else {
            "h:mm\naa"
        }

        val formatter = SimpleDateFormat(s)

        if (App.instance.storage.forceTimeZone) {
            val timezone = App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"
            formatter.timeZone = TimeZone.getTimeZone(timezone)
        }

        return formatter.format(date)
    }
}
