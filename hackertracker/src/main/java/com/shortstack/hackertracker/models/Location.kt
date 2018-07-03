package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Chris on 7/2/2018.
 */
@Entity
data class Location(
        @PrimaryKey
        val id: Int,
        val name: String,
        val conference: String
)