package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

/**
 * Created by Chris on 3/31/2018.
 */
@Entity
data class Conference(
        @PrimaryKey(autoGenerate = true)
        val index: Int,
        val title: String,
        val directory: String,
        val start: Date,
        val end: Date,
        val updated: Date,
        var isSelected: Boolean
)