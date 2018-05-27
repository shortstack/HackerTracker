package com.shortstack.hackertracker.models.response

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.models.Type
import java.io.Serializable

data class Types(
        @SerializedName("event_types")
        val types: List<Type>,
        @SerializedName("update_date")
        val date: String
) : Serializable