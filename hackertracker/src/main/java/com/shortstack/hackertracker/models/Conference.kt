package com.shortstack.hackertracker.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Created by Chris on 3/31/2018.
 */
@Entity
data class Conference(
        val title: String,
        @PrimaryKey(autoGenerate = false)
        val directory: String,
        val start: Date,
        val end: Date,
        val updated: Date,
        val synced: Date?,
        @Embedded
        val maps: ConferenceMap,
        var isSelected: Boolean
) {
    override fun toString() = title

    val index
        get() = directory.hashCode()
}