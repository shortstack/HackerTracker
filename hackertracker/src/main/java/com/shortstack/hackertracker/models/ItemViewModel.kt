package com.shortstack.hackertracker.models

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.now
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.utils.TimeUtil
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ItemViewModel(val item: Event) {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.myComponent.inject(this)
    }

    val title: String
        get() {
            val title = "[" + item.con + "] " + item.title
            if (!TextUtils.isEmpty(title) && title!!.endsWith("\n"))
                return title.substring(0, title.indexOf("\n"))
            return title!!
        }

    val description: String
        get() {
            var description: String = item.description!!
            description = description.replace("[.]. {2}".toRegex(), ".\n\n")
            description = description.replace("\n ".toRegex(), "\n")
            return description
        }

    fun getTimeStamp(context: Context): String {
        // No start time, return TBA.
        if (item.begin == null)
            return context.resources.getString(R.string.tba)

        return if (/*storage.shouldShowMilitaryTime()*/false) {
            item.begin.toString()
        } else {
            val date = item.begin
            val writeFormat = SimpleDateFormat("h:mm aa")
            writeFormat.format(date)
        }
    }


    val progress: Float
        get() {
            if (!item.hasStarted)
                return 0f

            val beginDateObject = item.begin
            val endDateObject = item.end
            val currentDate = Date().now()

            val length = ((endDateObject.time - beginDateObject.time) / 1000 / 60).toFloat()
            val p = ((endDateObject.time - currentDate.time) / 1000 / 60).toFloat()

            if (p == 0f)
                return 1f

            val l = p / length

            return Math.min(1.0f, 1 - l)
        }


    fun getFullTimeStamp(context: Context): String {
        val begin = item.begin
        val end = item.end

        val timestamp = TimeUtil.getRelativeDateStamp(context, begin)

        return String.format(context.getString(R.string.timestamp_full), timestamp, getTimeStamp(context, begin), getTimeStamp(context, end))
    }


    val displayTitle: String
        get() = "[${item.con}] ${item.title}"


    fun hasDescription(): Boolean {
        return !TextUtils.isEmpty(item.description)
    }

    fun hasUrl(): Boolean {
        return !TextUtils.isEmpty(item.url)
    }


    fun getDetailsDescription(context: Context): String {
        var result = ""

        result += (item.title!! + "\n")

        result += (getFullTimeStamp(context) + "\n")
        if (item.location != null)
            result += (item.location + "\n")
        //result = result.concat(getType());


        return result
    }

    val location: String
        get() = item.location ?: "???"

    val id: Int
        get() = item.index

    val toolsVisibility: Int
        get() = View.GONE

    val exploitVisibility: Int
        get() = View.GONE

    val demoVisibility: Int
        get() = View.GONE

    val bookmarkVisibility: Int
        get() = View.INVISIBLE

    val speakers: Array<Speaker>?
        get() = null
    val type: String
        get() = item.type


    companion object {

        private val EMPTY_CATEGORY = 0

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
