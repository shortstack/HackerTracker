package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Chris on 3/31/2018.
 */
@Entity(foreignKeys = [(ForeignKey(
        entity = Type::class,
        parentColumns = [("type")],
        childColumns = [("type")]))])
data class Event(
        @PrimaryKey(autoGenerate = false)
        val index: Int,
        @SerializedName("entry_type")
        val type: String,
        val title: String,
        val description: String,
        @SerializedName("start_date")
        val begin: String,
        @SerializedName("end_date")
        val end: String,

        val location: String?,
        val url: String?,
        val includes: String?
) : Serializable