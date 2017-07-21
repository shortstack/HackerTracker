package com.shortstack.hackertracker.Network

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.Model.Item

class SyncResponse {

    @SerializedName("update_date")
    var updatedDate: String? = null

    lateinit var schedule: Array<Item>


    override fun toString(): String {
        return updatedDate + ", " + schedule.size
    }
}
