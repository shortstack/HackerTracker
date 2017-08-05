package com.shortstack.hackertracker.Model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Types : Serializable {

    @SerializedName("event_types")
    lateinit var types: Array<Type>

    @SerializedName("update_date")
    lateinit var date: String


    class Type {

        @SerializedName("event_type")
        lateinit var type: String

    }
}