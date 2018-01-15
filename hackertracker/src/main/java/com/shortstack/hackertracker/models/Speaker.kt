package com.shortstack.hackertracker.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Speakers(
        val speakers: Array<Speaker>
)

data class Speaker(

        @SerializedName("sptitle")
        val title: String,
        @SerializedName("who")
        val name: String,
        @SerializedName("indexsp")
        val id: Int,
        val lastUpdate: String,
        val media: String,
        val bio: String

) : Serializable

