package com.shortstack.hackertracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Chris on 7/2/2018.
 */
@Entity
data class Location(
        @PrimaryKey
        val id: Int,
        val conference: String,
        val location: String,
        @SerializedName("updated_at")
        val updatedAt: Date
)