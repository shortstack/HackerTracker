package com.shortstack.hackertracker.models.response

import com.google.gson.annotations.SerializedName
import com.shortstack.hackertracker.models.Type

data class Types(
        @SerializedName("event_types")
        val types: List<Type>,
        @SerializedName("updated_at")
        val date: String
)