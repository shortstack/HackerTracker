package com.shortstack.hackertracker.Network

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.Model.Item

data class SyncResponse(

    @SerializedName("update_date")
    val updatedDate : String,
    val schedule: Array<Item>

)
