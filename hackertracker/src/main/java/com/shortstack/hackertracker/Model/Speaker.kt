package com.shortstack.hackertracker.Model

import com.google.gson.annotations.SerializedName

import java.io.Serializable

public class Speakers {

    lateinit var speakers : Array<Speaker>

    inner class Speaker : Serializable {

        @SerializedName("sptitle")
        lateinit var title: String
        @SerializedName("who")
        lateinit var name: String
        @SerializedName("indexsp")
        val id: Int = 0

        lateinit var lastUpdate: String
        lateinit var media: String
        lateinit var bio: String

    }
}
