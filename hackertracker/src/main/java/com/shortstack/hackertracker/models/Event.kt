package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.now
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Parcelize
@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("code")], childColumns = [("conference")], onDelete = ForeignKey.CASCADE))])
data class Event(
        @PrimaryKey
        val id: Int,
        @SerializedName("event_type")
        val type: Int,
        val title: String,
        val description: String,
        @SerializedName("start_date")
        val begin: Date,
        @SerializedName("end_date")
        val end: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,

        val location: Int,
        val url: String?,
        val conference: String,

        @Ignore
        val speakers: List<Int>,

        var isBookmarked: Boolean
) : Parcelable {

    constructor(id: Int, type: Int, title: String, description: String, begin: Date, end: Date, updatedAt: Date, location: Int, url: String?, conference: String) : this(id, type, title, description, begin, end, updatedAt, location, url, conference, emptyList(), false)

    val date: Date
        get() {
            val instance = Calendar.getInstance()
            instance.time = begin

            instance.set(Calendar.HOUR_OF_DAY, 0)
            instance.set(Calendar.MINUTE, 0)
            instance.set(Calendar.SECOND, 0)
            instance.set(Calendar.MILLISECOND, 0)


            return instance.time
        }

    val hasStarted: Boolean
        get() = Date().now().after(begin)

    val hasFinished: Boolean
        get() = Date().now().after(end)

    val notificationTime: Int
        get() {
            val current = Calendar.getInstance()

            val calendar = Calendar.getInstance()
            calendar.time = begin

            return ((calendar.timeInMillis - current.timeInMillis) / 1000).toInt()
        }

}