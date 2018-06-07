package com.shortstack.hackertracker.network

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.models.Event

data class SyncResponse(

        @SerializedName("update_date")
        val updatedDate: String,
        @SerializedName("schedule")
        val events: List<Event>

)
