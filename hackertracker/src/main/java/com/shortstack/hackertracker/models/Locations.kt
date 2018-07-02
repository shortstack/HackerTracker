package com.shortstack.hackertracker.models

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Chris on 7/2/2018.
 */
data class Locations(
        val locations: List<Location>,
        @SerializedName("update_date")
        val updatedAt: Date
)