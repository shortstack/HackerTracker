package com.shortstack.hackertracker.utils

import android.annotation.SuppressLint
import android.content.Context
import com.shortstack.hackertracker.*
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    private const val SOON_DAYS_AMOUNT = 5

    @SuppressLint("SimpleDateFormat")
    fun getRelativeDateStamp(context: Context, date: Date): String {
        if (date.isToday())
            return context.getString(R.string.today)

        if (date.isTomorrow())
            return context.getString(R.string.tomorrow)

        val format = SimpleDateFormat(if (date.isSoonish(SOON_DAYS_AMOUNT)) "EEEE" else "MMMM dd")

        return format.format(date)
    }
}
