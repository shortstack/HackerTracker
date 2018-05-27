package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.now
import java.io.Serializable
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
@Entity
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
) : Serializable {

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
        get() = begin.after(Date().now())

}