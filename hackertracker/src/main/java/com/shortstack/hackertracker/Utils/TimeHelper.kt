package com.shortstack.hackertracker.utils

import android.annotation.SuppressLint
import android.content.Context
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.Constants
import java.text.SimpleDateFormat
import java.util.*

class TimeHelper(mContext: Context) {

    val todayString: String = mContext.getString(R.string.today)
    val tomorrowString: String = mContext.getString(R.string.tomorrow)

    val currentDate: Date
        get() {
            val date = Date()
            if (BuildConfig.DEBUG)
                date.time = Constants.DEBUG_FORCE_TIME_DATE

            return date
        }

    val currentCalendar: Calendar
        get() {
            val instance = Calendar.getInstance()
            instance.time = currentDate
            return instance
        }


    @SuppressLint("SimpleDateFormat")
    fun getRelativeDateStamp(date: Date): String {
        if (date.isToday())
            return todayString

        if (date.isTomorrow())
            return tomorrowString

        val format = SimpleDateFormat(if (date.isSoonish(SOON_DAYS_AMOUNT)) "EEEE" else "MMMM dd")

        return format.format(date)
    }

    companion object {
        val SOON_DAYS_AMOUNT = 5
    }
}
