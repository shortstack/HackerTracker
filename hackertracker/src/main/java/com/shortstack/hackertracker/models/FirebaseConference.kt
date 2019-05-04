package com.shortstack.hackertracker.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.now
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

@Parcelize
data class FirebaseConference(
        val id: Int = 0,
        val name: String = "",
        val description: String = "",
        val code: String = "",
        @field:JvmField
        @PropertyName("is_selected")
        var isSelected: Boolean = false,
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
        get() = progress >= 1.0f

    val hasStarted: Boolean
        get() = progress >= 0f

    val progress: Float
        get() {
            val currentDate = Date().now()

            val length = ((finish.time - start.time) / 1000 / 60).toFloat()
            val p = ((finish.time - currentDate.time) / 1000 / 60).toFloat()

            if (p <= 0f)
                return p

            val l = p / length

            return 1 - l
        }
}

@Parcelize
data class FirebaseType(
        val id: Int = -1,
        val name: String = "",
        val conference: String = "",
        val color: String = "#343434",
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
) : Parcelable {

    val id: Int
        get() = hashCode().absoluteValue
}

@Parcelize
data class FirebaseMap(
        val name: String = "",
        val file: String = ""
) : Parcelable

@Parcelize
data class FirebaseBookmark(
        val id: String = "",
        val value: Boolean = false
) : Parcelable