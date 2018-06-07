package com.shortstack.hackertracker.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
        val bio: String?,
        var con: String
) : Parcelable

