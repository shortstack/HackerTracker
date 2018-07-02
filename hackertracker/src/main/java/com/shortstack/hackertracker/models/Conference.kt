package com.shortstack.hackertracker.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.database.ConferenceFile
import java.util.Date

/**
 * Created by Chris on 3/31/2018.
 */
@Entity
data class Conference(
        val id: Int,
        val name: String,
        val description: String,
        val timezone: String,
        @PrimaryKey(autoGenerate = false)
        val code: String,
        @SerializedName("start_date")
        val start: Date,
        @SerializedName("end_date")
        val end: Date,
        @SerializedName("updated_at")
        val updated: Date,

//        @Embedded
//        val maps: ConferenceMap,

        @Embedded(prefix = "locations_")
        val locations: ConferenceFile,
        @Embedded(prefix = "types_")
        @SerializedName("event_types")
        val types: ConferenceFile,
        @Embedded(prefix = "events_")
        val events: ConferenceFile,
        @Embedded(prefix = "speakers_")
        val speakers: ConferenceFile,
        @Embedded(prefix = "vendors_")
        val vendors: ConferenceFile,

        val synced: Date?,

        var isSelected: Boolean
) {
    override fun toString() = name

}