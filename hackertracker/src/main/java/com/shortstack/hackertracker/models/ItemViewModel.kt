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

class ItemViewModel(val item : Item) {


    val title : String
        get() {
            val title = item.title
            if (!TextUtils.isEmpty(title) && title!!.endsWith("\n"))
                return title.substring(0, title.indexOf("\n"))
            return title!!
        }

    val description : String
        get() {
            var description : String = item.description!!
            description = description.replace("[.]. {2}".toRegex(), ".\n\n")
            description = description.replace("\n ".toRegex(), "\n")
            return description
        }

    val categoryColorPosition : Int
        get() {
            if (TextUtils.isEmpty(item.type))
                return EMPTY_CATEGORY

            val types = App.application.databaseController.types

            for (i in types.indices) {
                if (item.type == types[i].type)
                    return i
            }

            return EMPTY_CATEGORY
        }


    fun getTimeStamp(context : Context) : String {
        // No start time, return TBA.
        if (TextUtils.isEmpty(item.begin))
            return context.resources.getString(R.string.tba)

        var time = ""

        if (App.application.storage.shouldShowMilitaryTime()) {
            time = item.begin!!
        } else {
            val date = item.beginDateObject
            if (date != null) {
                val writeFormat = SimpleDateFormat("h:mm aa")
                time = writeFormat.format(date)
            }
        }

        return time
    }


    val progress : Float
        get() {
            if (!item.hasBegin())
                return 0f

            val beginDateObject = item.beginDateObject
            val endDateObject = item.endDateObject
            val currentDate = App.getCurrentDate()

            val length = ((endDateObject.time - beginDateObject.time) / 1000 / 60).toFloat()
            val p = ((endDateObject.time - currentDate.time) / 1000 / 60).toFloat()

            if (p == 0f)
                return 1f

            val l = p / length

            return Math.min(1.0f, 1 - l)
        }

    fun getFullTimeStamp(context : Context) : String {
        val begin = item.beginDateObject
        val end = item.endDateObject

        return String.format(context.getString(R.string.timestamp_full), item.dateStamp, getTimeStamp(context, begin), getTimeStamp(context, end))
    }


    /*(BuildConfig.DEBUG ? mItem.getIndex() + " " : "") +*/ val displayTitle : String
        get() = item.title!!

    fun hasDescription() : Boolean {
        return !TextUtils.isEmpty(item.description)
    }

    fun hasUrl() : Boolean {
        return !TextUtils.isEmpty(item.link)
    }

    val prettyUrl : String
        get() {
            var url = item.link!!.toLowerCase()

            var index : Int


            if (url.startsWith("http://") || url.startsWith("https://")) {
                index = url.indexOf("//")
                url = url.substring(index + 2)
            }

            index = url.indexOf("www.")
            if (index > 0)
                url = url.substring(index)

            index = url.indexOf("/")
            if (index > 1) {

                val p = Pattern.compile("[\\./?]")
                val m = p.matcher(url.substring(index + 1))

                if (m.find()) {
                    url = url.substring(0, index + m.start() + 1)
                }
            }

            if (url.length < item.link.length) {
                url = url + "..."
            }


            return url
        }

    fun getDetailsDescription(context : Context) : String {
        var result = ""

        result = result + (item.title!! + "\n")

        result = result + (getFullTimeStamp(context) + "\n")
        result = result + (item.location!! + "\n")
        //result = result.concat(getType());


        return result
    }

    val location : String
        get() = item.location ?: ""

    val id : Int
        get() = item.index

    val toolsVisibility : Int
        get() = if (item.isTool) View.VISIBLE else View.GONE

    val exploitVisibility : Int
        get() = if (item.isExploit) View.VISIBLE else View.GONE

    val demoVisibility : Int
        get() = if (item.isDemo) View.VISIBLE else View.GONE

    val bookmarkVisibility : Int
        get() = if (item.isBookmarked()) View.VISIBLE else View.INVISIBLE

    val speakers : Array<Speaker>
        get() = item.speakers!!

    companion object {

        private val EMPTY_CATEGORY = 0

        fun getTimeStamp(context : Context, date : Date?) : String {
            // No start time, return TBA.
            if (date == null)
                return context.resources.getString(R.string.tba)

            val time : String
            val writeFormat : DateFormat

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
