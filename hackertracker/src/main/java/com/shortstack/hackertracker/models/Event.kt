package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

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
        var con : String
) : Serializable