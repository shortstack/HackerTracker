package com.shortstack.hackertracker.models

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.TimeUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventViewModel(val event: DatabaseEvent) : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val title: String
        get() = "[${event.event.con}] ${event.event.title}"

    val description: String
        get() = event.event.description

    val progress: Float
        get() {
            if (!event.event.hasStarted)
                return 0f

            val currentDate = Date().now()

            val length = ((event.event.end.time - event.event.begin.time) / 1000 / 60).toFloat()
            val p = ((event.event.end.time - currentDate.time) / 1000 / 60).toFloat()

            if (p == 0f)
                return 1f

            val l = p / length

            return Math.min(1.0f, 1 - l)
        }


    fun getFullTimeStamp(context: Context): String {
        val begin = event.event.begin
        val end = event.event.end

        val timestamp = TimeUtil.getRelativeDateStamp(context, begin)

        return String.format(context.getString(R.string.timestamp_full), timestamp, getTimeStamp(context, begin), getTimeStamp(context, end))
    }

    fun hasDescription() = !TextUtils.isEmpty(event.event.description)

    fun hasUrl() = !TextUtils.isEmpty(event.event.url)


    fun getDetailsDescription(context: Context): String {
        var result = ""

        result += (event.event.title + "\n")

        result += (getFullTimeStamp(context) + "\n")
        if (event.event.location != null)
            result += (event.event.location + "\n")
        //result = result.concat(getType());


        return result
    }

    val location: String
        get() = event.event.location ?: "[Unknown]"

    val id: Int
        get() = event.event.index

    val toolsVisibility: Int
        get() = if (event.event.includes?.contains("tool") == true) View.VISIBLE else View.GONE

    val exploitVisibility: Int
        get() = if (event.event.includes?.contains("exploit") == true) View.VISIBLE else View.GONE

    val demoVisibility: Int
        get() = if (event.event.includes?.contains("demo") == true) View.VISIBLE else View.GONE

    val bookmarkVisibility: Int
        get() = if (event.event.isBookmarked) View.VISIBLE else View.INVISIBLE

    val speakers: Array<Speaker>?
        get() = null

    val type: String
        get() = type


    companion object {

        fun getTimeStamp(context: Context, date: Date?): String {
            // No start time, return TBA.
            if (date == null)
                return context.resources.getString(R.string.tba)

            return if (android.text.format.DateFormat.is24HourFormat(context)) {
                SimpleDateFormat("HH:mm").format(date)
            } else {
                SimpleDateFormat("h:mm aa").format(date)
            }
        }
    }

}
