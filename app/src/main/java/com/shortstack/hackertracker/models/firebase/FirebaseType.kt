package com.shortstack.hackertracker.models.firebase

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseType(
        val id: Int = -1,
        val name: String = "",
        val description: String = "",
        val conference: String = "",
        val color: String = "#343434",
        val discord_url: String? = null,
        val subforum_url: String? = null,
        val tags: String? = null
) : Parcelable