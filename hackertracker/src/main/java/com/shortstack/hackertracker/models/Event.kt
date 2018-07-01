package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.now
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Parcelize
@Entity(foreignKeys = [(ForeignKey(entity = (Conference::class), parentColumns = [("directory")], childColumns = [("con")], onDelete = ForeignKey.CASCADE))])
data class Event(
        @PrimaryKey(autoGenerate = false)
        val index: Int,
        @SerializedName("entry_type")
        val type: String,
        val title: String,
        val description: String,
        @SerializedName("start_date")
        val begin: Date,
        @SerializedName("end_date")
        val end: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,

        val location: String?,
        val url: String?,
        val includes: String?,

        var isBookmarked: Boolean,
        var con: String
) : Parcelable {

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