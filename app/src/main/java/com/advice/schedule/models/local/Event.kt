package com.advice.schedule.models.local

import android.content.Context
import android.os.Parcelable
import com.advice.schedule.utilities.MyClock
import com.advice.schedule.utilities.TimeUtil
import com.advice.schedule.utilities.getDateMidnight
import com.advice.schedule.utilities.now
import com.google.firebase.Timestamp
import com.shortstack.hackertracker.R
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Event(
    val id: Long = -1,
    val conference: String,
    val title: String,
    val _description: String,
    val start: Timestamp,
    val end: Timestamp,
    val updated: String,
    val speakers: List<Speaker>,
    val types: List<Type>,
    val location: Location,
    val urls: List<Action>,
    var isBookmarked: Boolean = false,
    var key: Long = -1
) : Parcelable {

    val progress: Float
        get() {
            return 0f
        }

    val hasFinished: Boolean
        get() {
            return end.compareTo(Timestamp.now()) == 1
        }

    val hasStarted: Boolean
        get() =  start.compareTo(Timestamp.now()) == -1

    val date: Date
        get() {
            return Calendar.getInstance().apply {
                time = start.toDate()

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

    val adjustedDate: Date
        get() {
            return getDateMidnight(start.toDate())
        }

    fun getFullTimeStamp(context: Context): String {
        val date = TimeUtil.getDateStamp(start.toDate())

        val time = if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm").format(start.toDate())
        } else {
            SimpleDateFormat("h:mm aa").format(start.toDate())
        }

        return String.format(context.getString(R.string.timestamp_start), date, time)
    }

    val description: String
        get() {
            if (urls.isEmpty())
                return _description

            val firstUrl = urls.minByOrNull { _description.indexOf(it.url) } ?: return _description
            val end = _description.indexOf(firstUrl.url)
            if (end == -1) {
                return _description
            }

            val newLine = _description.substring(0, end).lastIndexOf("\n")
            if (newLine == -1) {
                return _description
            }

            return _description.substring(0, newLine).trim()
        }
}