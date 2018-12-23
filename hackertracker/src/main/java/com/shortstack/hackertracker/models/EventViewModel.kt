package com.shortstack.hackertracker.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.TimeUtil
import java.util.*
import javax.inject.Inject

class EventViewModel(val event: FirebaseEvent) : ViewModel() {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.component.inject(this)
    }

    val title: String
        get() = event.title

    val description: String
        get() = event.description

    val progress: Float
        get() {
            if (!event.hasStarted)
                return 0f

            val currentDate = Date().now()

            val length = ((event.finish.time - event.start.time) / 1000 / 60).toFloat()
            val p = ((event.finish.time - currentDate.time) / 1000 / 60).toFloat()

            if (p == 0f)
                return 1f

            val l = p / length

            return Math.min(1.0f, 1 - l)
        }

    var hasAnimatedProgress: Boolean = true


    fun getFullTimeStamp(context: Context): String {
        val (begin, end) = getTimeStamp(context)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }


    fun getTimeStamp(context: Context): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.start)
        return Pair(begin, end)
    }

    val location: String
        get() = event.location.name


    val id: Int
        get() = event.id

    val speakers: ArrayList<FirebaseSpeaker>
        get() = event.speakers
}
