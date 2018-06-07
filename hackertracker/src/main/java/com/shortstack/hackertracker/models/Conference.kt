package com.shortstack.hackertracker.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
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

    @Ignore
    val index = directory.hashCode()
}