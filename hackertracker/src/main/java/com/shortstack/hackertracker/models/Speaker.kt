package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Speaker(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("indexsp")
        val id: Int,
        @SerializedName("sptitle")
        val title: String?,
        @SerializedName("who")
        val name: String,
        val lastUpdate: String,
        val media: String?,
        val bio: String?

) : Serializable

