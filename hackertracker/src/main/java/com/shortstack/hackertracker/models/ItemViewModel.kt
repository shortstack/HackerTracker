package com.shortstack.hackertracker.models

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ItemViewModel(val item: Event) {


    val title: String
        get() {
            val title = item.title
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

        var time = ""

//        if (App.application.storage.shouldShowMilitaryTime()) {
//            time = item.begin!!
//        } else {
//            val date = item.beginDateObject
//            if (date != null) {
//                val writeFormat = SimpleDateFormat("h:mm aa")
//                time = writeFormat.format(date)
//            }
//        }

        return time
    }


    val progress: Float
        get() {
//            if (!item.hasBegin())
            return 0f

//            val beginDateObject = item.beginDateObject
//            val endDateObject = item.endDateObject
//            val currentDate = App.getCurrentDate()
//
//            val length = ((endDateObject.time - beginDateObject.time) / 1000 / 60).toFloat()
//            val p = ((endDateObject.time - currentDate.time) / 1000 / 60).toFloat()
//
//            if (p == 0f)
//                return 1f
//
//            val l = p / length
//
//            return Math.min(1.0f, 1 - l)
        }

    fun getFullTimeStamp(context: Context): String {
//        val begin = item.beginDateObject
//        val end = item.endDateObject
//
        return String.format(context.getString(R.string.timestamp_full), App.getRelativeDateStamp(item.begin), getTimeStamp(context, item.begin), getTimeStamp(context, item.end))
    }


    /*(BuildConfig.DEBUG ? mItem.getIndex() + " " : "") +*/ val displayTitle: String
        get() = item.title!!

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
        get() = item.location ?: ""

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

            val time: String
            val writeFormat: DateFormat

            if (App.application.storage.shouldShowMilitaryTime()) {
                writeFormat = SimpleDateFormat("HH:mm")
            } else {
                writeFormat = SimpleDateFormat("h:mm aa")
            }

            time = writeFormat.format(date)

            return time
        }
    }

}
