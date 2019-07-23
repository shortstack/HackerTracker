package com.shortstack.hackertracker.models.local

import android.content.Context
import android.os.Parcelable
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utils.MyClock
import com.shortstack.hackertracker.utils.TimeUtil
import com.shortstack.hackertracker.utils.now
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Event(
        val id: Int = -1,
        val conference: String,
        val title: String,
        val description: String,
        val start: Date,
        val end: Date,
        val link: String,
        val updated: String,
        val speakers: List<Speaker>,
        val type: Type,
        val location: Location,
        var isBookmarked: Boolean = false,
        var key: Long = -1) : Parcelable {

    val progress: Float
        get() {
            val currentDate = MyClock().now()

            if(currentDate.before(start))
                return -1f

            if(currentDate.after(end))
                return 1.0f

            val length = ((end.time - start.time) / 1000 / 60).toFloat()
            val p = ((end.time - currentDate.time) / 1000 / 60).toFloat()

            val l = p / length

            return Math.min(1.0f, 1 - l)
        }

    val hasFinished: Boolean
        get() = progress >= 1.0f

    val hasStarted: Boolean
        get() = progress >= 0.0f

    val date: Date
        get() {
            return Calendar.getInstance().apply {
                time = start

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

    fun getFullTimeStamp(context: Context): String {
        val date = TimeUtil.getDateStamp(start)

        val time = if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm").format(start)
        } else {
            SimpleDateFormat("h:mm aa").format(start)
        }

        return String.format(context.getString(R.string.timestamp_full), date, time)
    }
}