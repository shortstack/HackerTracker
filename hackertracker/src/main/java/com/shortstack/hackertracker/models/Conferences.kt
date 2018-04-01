package com.shortstack.hackertracker.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Created by Chris on 3/31/2018.
 */
data class Conferences(
        @SerializedName("conferences")
        val conferences: List<Conference>,
        @SerializedName("update_date")
        val updatedAt: Date
)