package com.shortstack.hackertracker.models

import com.google.gson.annotations.SerializedName

data class Type(
        @SerializedName("event_type")
        var type : String)