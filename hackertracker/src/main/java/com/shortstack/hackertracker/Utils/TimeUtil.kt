package com.shortstack.hackertracker.utils

import android.annotation.SuppressLint
import android.content.Context
import com.shortstack.hackertracker.*
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

        return if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm").format(date)
        } else {
            SimpleDateFormat("h:mm aa").format(date)
        }
    }
}
