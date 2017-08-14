package com.shortstack.hackertracker.Model

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Speakers {

    lateinit var speakers : Array<Speaker>

    inner class Speaker : Serializable {

        @SerializedName("sptitle")
        private val title: String? = null
        @SerializedName("who")
        val name: String? = null
        @SerializedName("indexsp")
        val id: Int = 0

        private val lastUpdate: String? = null
        private val media: String? = null
        val bio: String? = null

    }
}
