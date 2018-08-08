package com.shortstack.hackertracker.models

import androidx.lifecycle.ViewModel
import android.content.Context
import android.text.TextUtils
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.TimeUtil
import java.util.*
import javax.inject.Inject

class EventViewModel(val event: DatabaseEvent) : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val title: String
        get() = event.event.title

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

    var hasAnimatedProgress: Boolean = true


    fun getFullTimeStamp(context: Context): String {
        val (begin, end) = getTimeStamp(context)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.event.begin)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }


    fun getTimeStamp(context: Context): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.event.begin)
        val end = TimeUtil.getTimeStamp(context, event.event.end)
        return Pair(begin, end)
    }

    val location: String
        get() = event.location.firstOrNull()?.name ?: "Unknown"

    val id: Int
        get() = event.event.id

    val speakers: Array<Speaker>?
        get() = null
}
