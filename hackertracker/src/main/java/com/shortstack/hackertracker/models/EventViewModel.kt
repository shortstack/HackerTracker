package com.shortstack.hackertracker.models

import android.content.Context
import androidx.lifecycle.ViewModel
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.TimeUtil
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

class EventViewModel(var event: FirebaseEvent?) : ViewModel(), KoinComponent {

    private val database: DatabaseManager by inject()

    val title: String?
        get() = event?.title

    val description: String?
        get() = event?.description

    val progress: Float
        get() {
            val event = event ?: return 0f

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
        val event = event ?: return "???"

        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }


    private fun getTimeStamp(context: Context, event: FirebaseEvent): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.start)
        return Pair(begin, end)
    }

    val location: String?
        get() = event?.location?.name


    val id: Int
        get() = event?.id ?: -1

    val speakers: ArrayList<FirebaseSpeaker>
        get() = event?.speakers ?: ArrayList()

    val type: FirebaseType
        get() = event?.type ?: FirebaseType(id = -1, name = "???", color = "#343434")

    val isBookmarked: Boolean
        get() = event?.isBookmarked ?: false
}
