package com.shortstack.hackertracker.network

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.models.Item

data class SyncResponse(

    @SerializedName("update_date")
    val updatedDate : String,
    val schedule: Array<Item>

)
