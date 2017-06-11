package com.shortstack.hackertracker.Network

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.Model.Item

class SyncResponse {

    @SerializedName("updateTime")
    var time: String? = null
    @SerializedName("updateDate")
    var date: String? = null

    lateinit var schedule: Array<Item>


    override fun toString(): String {
        return time + ", " + date + ", " + schedule!!.size
    }
}
