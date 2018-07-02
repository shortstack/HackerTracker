package com.shortstack.hackertracker.database

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Chris on 7/1/2018.
 */
data class ConferenceFile(
        val link: String,
        @SerializedName("updated_at")
        val updatedAt: Date
)