package com.shortstack.hackertracker.models

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Chris on 3/31/2018.
 */
data class Events(
        @SerializedName("update_date")
        val updateDate: String,
        @SerializedName("schedule")
        val events: List<Event>
)