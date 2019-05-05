package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.shortstack.hackertracker.now
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class FirebaseEvent(
        val id: Int = -1,
        val conference: String = "",
        val title: String = "",
        val description: String = "",
        val begin: String = "",
        val end: String = "",
        val link: String = "",
        val updated: String = "",
        @field:JvmField
        @PropertyName("is_bookmarked")
        var isBookmarked: Boolean = false,
        val speakers: ArrayList<FirebaseSpeaker> = ArrayList(),
        val type: FirebaseType = FirebaseType(),
        val location: FirebaseLocation = FirebaseLocation()
) : Parcelable {

    val start: Date
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(begin)

    val finish: Date
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(end)

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


    val hasFinished: Boolean
        get() {
            val currentDate = Date().now()

            val length = ((finish.time - start.time) / 1000 / 60).toFloat()
            val p = ((finish.time - currentDate.time) / 1000 / 60).toFloat()

            if (p == 0f)
                return false

            val l = p / length

            return 1 - l >= 1.0f
        }

    val notificationTime: Long
        get() = 0L // TODO: Return the amount of milliseconds until this event starts.

    val hasStarted: Boolean
        get() = true // TODO: Check if the event has started yet.
}