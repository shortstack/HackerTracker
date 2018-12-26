package com.shortstack.hackertracker.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Chris on 2018-12-15.
 */
@Parcelize
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        @field:JvmField
        @PropertyName("is_selected")
        val isSelected: Boolean = false,
        val maps: ArrayList<FirebaseMap> = ArrayList()
//        val events: HashMap<String, FirebaseEvent> = HashMap(),
//        val types: HashMap<String, FirebaseType> = HashMap()
) : Parcelable

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
        get() = false // TODO: Return if the event has finished.

    val notificationTime: Long
        get() = 0L // TODO: Return the amount of milliseconds until this event starts.

    val hasStarted: Boolean
        get() = true // TODO: Check if the event has started yet.
}

@Parcelize
data class FirebaseType(
        val id: Int = -1,
        val name: String = "",
        val conference: String = "",
        val color: String = "",
        @field:JvmField
        @PropertyName("is_selected")
        var isSelected: Boolean = false


) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return (other as? FirebaseType)?.id == id || super.equals(other)
    }
}

@Parcelize
data class FirebaseLocation(
        val name: String = "",
        val conference: String = ""
) : Parcelable

@Parcelize
data class FirebaseSpeaker(
        val name: String = "",
        val description: String = "",
        val link: String = "",
        val twitter: String = "",
        val title: String = "",

        val events: ArrayList<FirebaseEvent> = ArrayList()
) : Parcelable

@Parcelize
data class FirebaseMap(
        val name: String = "",
        val file: String = ""
) : Parcelable