package com.shortstack.hackertracker.models.local

import android.content.Context
import android.os.Parcelable
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseLocation
import com.shortstack.hackertracker.models.firebase.FirebaseSpeaker
import com.shortstack.hackertracker.models.firebase.FirebaseType
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.TimeUtil
import kotlinx.android.parcel.Parcelize
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
        val speakers: ArrayList<FirebaseSpeaker>,
        val type: FirebaseType,
        val location: FirebaseLocation,
        var isBookmarked: Boolean = false) : Parcelable {

    val progress: Float
        get() {
            val currentDate = Date().now()

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
        val (begin, end) = getTimeStamp(context)
        val timestamp = TimeUtil.getRelativeDateStamp(context, start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    private fun getTimeStamp(context: Context): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, start)
        val end = TimeUtil.getTimeStamp(context, end)
        return Pair(begin, end)
    }
}